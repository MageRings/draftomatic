package magic.data.database;

import java.io.IOException;
import java.util.Map;

import magic.data.tournament.TournamentData;

public interface Database {

    void writeTournament(TournamentData tournament) throws IOException;

    Map<String, TournamentData> loadTournaments() throws IOException;
}
