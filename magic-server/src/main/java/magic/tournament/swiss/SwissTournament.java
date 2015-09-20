package magic.tournament.swiss;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
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
        return calculator.innerCalculatePairings(playersAtEachPointLevel, tieBreakers, pointsPerPlayer, alreadyMatched);
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
}
