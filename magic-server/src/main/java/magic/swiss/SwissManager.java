package magic.swiss;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;

import magic.data.Player;
import magic.data.Format;
import magic.exceptions.TournamentNotFoundException;

public class SwissManager {

    ConcurrentMap<String, SwissTournament> runningTournaments = Maps.newConcurrentMap();

    public String registerTournament(Optional<Integer> rounds, Optional<Format> format, Optional<String> formatCode, Collection<Player> players) {
        if (players == null || players.size() == 0) {
            throw new IllegalArgumentException("Must have at least one player to have a tournament!");
        }
        String uuid = UUID.randomUUID().toString();
        while (runningTournaments.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        int actualRounds;
        if (rounds.isPresent()) {
            actualRounds = rounds.get();
        } else {
            actualRounds = getDefaultNumberOfRounds(players.size());
        }
        runningTournaments.put(uuid, new SwissTournament(uuid, actualRounds, players));
        return uuid;
    }

    public SwissTournament getTournament(String tournamentId) {
        SwissTournament tournament = runningTournaments.get(tournamentId);
        if (tournament == null) {
            throw new TournamentNotFoundException(tournamentId);
        }
        return tournament;
    }

    @VisibleForTesting
    /* package */ static int getDefaultNumberOfRounds(int numberOfPlayers) {
        int closestSmallerPowerOfTwo = Integer.highestOneBit(numberOfPlayers);
        int minNumberOfRounds = 0;
        while (closestSmallerPowerOfTwo >> minNumberOfRounds > 1) {
            minNumberOfRounds++;
        }
        if (closestSmallerPowerOfTwo == numberOfPlayers) {
            return minNumberOfRounds;
        }
        return minNumberOfRounds + 1;
    }
}
