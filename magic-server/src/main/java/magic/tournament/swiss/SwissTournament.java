package magic.tournament.swiss;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    protected NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                           Map<Player, TieBreakers> tieBreakers) {
        return calculator
                .innerCalculatePairings(state, rankPlayers(getTournamentId(), state, tieBreakers));
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
     * Returns a map that gives the position of each player relative to the others. The lowest value
     * in the map corresponds to the player that is doing the best.
     *
     * @param tournamentId
     * @param pointsPerPlayer
     * @param tieBreakers
     * @return
     */
    private static LinkedHashMap<Player, Integer> rankPlayers(String tournamentId,
                                                              TournamentState state,
                                                              Map<Player, TieBreakers> tieBreakers) {
        LinkedHashMap<Player, Integer> rankings = Maps.newLinkedHashMap();
        List<Player> players = Lists.newArrayList(state.getPlayers());
        Collections.sort(players, (a, b) -> tieBreakers.get(a).compareTo(tieBreakers.get(b)));
        Collections.reverse(players);
        for (int i = 0; i < players.size(); i++) {
            rankings.put(players.get(i), i);
        }
        return rankings;
    }
}
