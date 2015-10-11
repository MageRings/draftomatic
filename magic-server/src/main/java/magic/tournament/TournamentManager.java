package magic.tournament;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import magic.data.database.Database;
import magic.data.database.FileSystemDB;
import magic.data.tournament.TournamentInput;
import magic.exceptions.TournamentNotFoundException;
import magic.tournament.swiss.GraphPairing;
import magic.tournament.swiss.SwissTournament;

public final class TournamentManager {

    private final ConcurrentMap<String, Tournament> runningTournaments = Maps.newConcurrentMap();
    private final Database                          db                 = new FileSystemDB();

    public TournamentManager() {
        try {
            this.db.loadTournaments().forEach((id, data) -> {
                this.runningTournaments.put(
                        id,
                        new SwissTournament(this.db, data, new GraphPairing()));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String registerTournament(TournamentInput input, Optional<Integer> numberOfRounds) {
        if (input.getPlayers().size() == 0) {
            throw new IllegalArgumentException("Must have at least one player to have a tournament!");
        }
        String uuid = UUID.randomUUID().toString();
        while (this.runningTournaments.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        Tournament t = new SwissTournament(this.db, uuid, input, numberOfRounds, new GraphPairing());
        t.initFirstRound();
        this.runningTournaments.put(uuid, t);
        return uuid;
    }

    public Tournament getTournament(String tournamentId) {
        Tournament tournament = this.runningTournaments.get(tournamentId);
        if (tournament == null) {
            throw new TournamentNotFoundException(tournamentId);
        }
        return tournament;
    }
}
