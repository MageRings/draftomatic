package magic.data.database;

import java.io.IOException;
import java.util.Map;

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

}
