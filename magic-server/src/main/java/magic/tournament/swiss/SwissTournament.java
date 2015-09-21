package magic.tournament.swiss;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import magic.data.Pairing;
import magic.data.Player;
import magic.tournament.AbstractTournament;
import magic.tournament.TieBreakers;

public class SwissTournament extends AbstractTournament {

    private final SwissPairingCalculator calculator;

    public SwissTournament(String tournamentId,
                           Optional<Integer> numberOfRounds,
                           Collection<Player> inputPlayers,
                           SwissPairingCalculator calculator) {
        super(
                tournamentId,
                numberOfRounds.isPresent() ? numberOfRounds.get() : getDefaultNumberOfRounds(inputPlayers.size()),
                    inputPlayers);
        this.calculator = calculator;
    }

    @Override
    protected NavigableSet<Pairing> innerCalculatePairings(Multimap<Integer, Player> playersAtEachPointLevel,
                                                           Optional<Map<Player, TieBreakers>> tieBreakers,
                                                           Map<Player, Integer> pointsPerPlayer,
                                                           Multimap<Player, Player> alreadyMatched) {
        return calculator.innerCalculatePairings(
                playersAtEachPointLevel,
                rankPlayers(getCurrentRound(), getTournamentId(), pointsPerPlayer, tieBreakers),
                pointsPerPlayer,
                alreadyMatched);
    }

    @VisibleForTesting
    protected static int getDefaultNumberOfRounds(int numberOfPlayers) {
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

    /**
     * Returns a map that gives the position of each player relative to the others. The lowest
     * value in the map corresponds to the player that is doing the best.
     *
     * @param round
     * @param tournamentId
     * @param pointsPerPlayer
     * @param tieBreakers
     * @return
     */
    private static Map<Player, Integer> rankPlayers(int round,
                                                    String tournamentId,
                                                    Map<Player, Integer> pointsPerPlayer,
                                                    Optional<Map<Player, TieBreakers>> tieBreakers) {
        Map<Player, Integer> rankings = Maps.newHashMapWithExpectedSize(pointsPerPlayer.size());
        List<Player> players = Lists.newArrayList(pointsPerPlayer.keySet());
        // check to see if this is the last round
        if (tieBreakers.isPresent()) {
            Collections.sort(players, (a, b) -> tieBreakers.get().get(a).compareTo(tieBreakers.get().get(b)));
        } else {
            Collections.sort(players, (a, b) -> {
                String aBreaker = TieBreakers.generateRandomTieBreaker(tournamentId, a.getId(), Optional.of(round));
                String bBreaker = TieBreakers.generateRandomTieBreaker(tournamentId, b.getId(), Optional.of(round));
                return aBreaker.compareTo(bBreaker);
            });
        }
        Collections.reverse(players);
        for (int i = 0; i < players.size(); i++) {
            rankings.put(players.get(i), i);
        }
        return rankings;
    }
}
