package magic.data.database;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import magic.data.Player;
import magic.data.tournament.TournamentData;

public class HerokuDB implements Database {

	private static final int DEFAULT_USER = 1;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Connection connection;

    public HerokuDB(String uri) throws URISyntaxException, SQLException {
        URI dbUri = new URI(uri);
        String usrname = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        this.connection = DriverManager.getConnection(dbUrl, usrname, password);
        try(Statement st = this.connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS players(usr integer PRIMARY KEY, names text);");
            st.execute("INSERT INTO players(usr, names) SELECT " + DEFAULT_USER + ", '[]' " +
            		"WHERE NOT EXISTS (SELECT 1 FROM players WHERE usr=" + DEFAULT_USER + ");");
            st.execute("CREATE TABLE IF NOT EXISTS tournaments(id text PRIMARY KEY, usr integer, data text);");
        }
    }
    
    private static final String INSERT_TOURNAMENT = "UPDATE tournaments SET data=? WHERE id=?; " +
    		"INSERT INTO tournaments (id, usr, data) " +
    		"SELECT ?, ?, ? " +
    		"WHERE NOT EXISTS (SELECT 1 FROM tournaments WHERE id=?);";

    @Override
    public void writeTournament(TournamentData tournament) throws IOException {
        try(PreparedStatement ps = this.connection.prepareStatement(INSERT_TOURNAMENT)) {
        	String tournamentData = MAPPER.writeValueAsString(tournament);
        	ps.setString(1, tournamentData);
        	ps.setString(2, tournament.getId());

        	ps.setString(3, tournament.getId());
        	ps.setInt(4, DEFAULT_USER);
        	ps.setString(5, tournamentData);
        	
        	ps.setString(6, tournament.getId());
        	ps.execute();
        } catch (SQLException e) {
        	throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, TournamentData> loadTournaments() throws IOException {
    	Map<String, TournamentData> result = Maps.newHashMap();
    	try(Statement st = this.connection.createStatement()) {
    		ResultSet rs = st.executeQuery("SELECT * FROM tournaments;");
    		while(rs.next()) {
    			TournamentData data = MAPPER.readValue(rs.getString("data"), TournamentData.class);
    			result.put(data.getId(), data);
    		}
    	} catch (SQLException e) {
    		throw new RuntimeException(e);
    	}
    	return result;
    }

    @Override
    public Set<Player> getPlayers() throws IOException {
    	try(Statement st = this.connection.createStatement()) {
    		ResultSet rs = st.executeQuery("SELECT * FROM players;");
    		if(!rs.next()) {
    			throw new IOException("Failed to read out players from the database!");
    		}
    		return MAPPER.readValue(rs.getString("names"), new TypeReference<Set<Player>>(){});
    	} catch (SQLException e) {
    		throw new RuntimeException(e);
    	}
    }

    @Override
    public Set<Player> registerPlayers(List<String> playerNames) throws IOException {
        lock.writeLock().lock();;
        try {
            Set<Player> currentPlayers = getPlayers();
            Set<Player> addedPlayers = DbUtils.registerPlayers(playerNames, currentPlayers);

            try(Statement st = this.connection.createStatement()) {
            	String playerData = MAPPER.writeValueAsString(Sets.union(currentPlayers, addedPlayers));
            	st.execute("UPDATE players SET names='" + playerData + "' WHERE usr=" + DEFAULT_USER + ";");
            } catch (SQLException e) {
            	throw new RuntimeException(e);
            }
            
            return addedPlayers;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
