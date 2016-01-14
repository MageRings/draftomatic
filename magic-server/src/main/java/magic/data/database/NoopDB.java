package magic.data.database;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import magic.data.Player;
import magic.data.tournament.TournamentData;

public final class NoopDB implements Database {

    public static final Database NOOPDB = new NoopDB();

    private NoopDB() {
    }

    @Override
    public void writeTournament(TournamentData tournament) throws IOException {
    }

    @Override
    public Map<String, TournamentData> loadTournaments() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Player> getPlayers() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Player> registerPlayers(List<String> playerNames) throws IOException {
        throw new UnsupportedOperationException();
    }

}
