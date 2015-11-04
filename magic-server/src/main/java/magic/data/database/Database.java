package magic.data.database;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import magic.data.Player;
import magic.data.tournament.TournamentData;

public interface Database {

    void writeTournament(TournamentData tournament) throws IOException;

    Map<String, TournamentData> loadTournaments() throws IOException;

    Set<Player> getPlayers() throws IOException;

    /**
     * @return A list of players assigned unique ids.
     */
    Set<Player> registerPlayers(List<String> playerNames) throws IOException;
}
