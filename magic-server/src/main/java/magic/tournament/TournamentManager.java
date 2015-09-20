package magic.tournament;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import magic.data.Format;
import magic.data.Player;
import magic.exceptions.TournamentNotFoundException;
import magic.tournament.swiss.ListSortingPairingSwiss;
import magic.tournament.swiss.SwissTournament;

public class TournamentManager {

    ConcurrentMap<String, Tournament> runningTournaments = Maps.newConcurrentMap();

    public String registerTournament(Optional<Integer> rounds,
                                     Optional<Format> format,
                                     Optional<String> formatCode,
                                     Collection<Player> players) {
        if (players == null || players.size() == 0) {
            throw new IllegalArgumentException("Must have at least one player to have a tournament!");
        }
        String uuid = UUID.randomUUID().toString();
        while (runningTournaments.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        runningTournaments.put(uuid, new SwissTournament(uuid, rounds, players, new ListSortingPairingSwiss()));
        return uuid;
    }

    public Tournament getTournament(String tournamentId) {
        Tournament tournament = runningTournaments.get(tournamentId);
        if (tournament == null) {
            throw new TournamentNotFoundException(tournamentId);
        }
        return tournament;
    }
}
