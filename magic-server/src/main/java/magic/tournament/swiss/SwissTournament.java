package magic.tournament.swiss;

import java.time.ZonedDateTime;
import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jersey.repackaged.com.google.common.collect.Sets;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.database.Database;
import magic.data.tournament.TournamentData;
import magic.data.tournament.TournamentInput;
import magic.tournament.AbstractTournament;
import magic.tournament.TieBreakers;

public class SwissTournament extends AbstractTournament {

    private final SwissPairingCalculator calculator;

    // used when rehydrating from the DB
    public SwissTournament(Database db,
                           TournamentData data,
                           SwissPairingCalculator calculator) {
        super(db, data);
        this.calculator = calculator;
    }

    public SwissTournament(Database db,
                           String tournamentId,
                           TournamentInput input,
                           Optional<Integer> numberOfRounds,
                           SwissPairingCalculator calculator) {
        this(
                db,
                new TournamentData(
                        tournamentId,
                        numberOfRounds.orElseGet(() -> SwissTournament.getDefaultNumberOfRounds(input.getPlayers().size())),
                        input,
                        ZonedDateTime.now(),
                        null,
                        Sets.newTreeSet()), calculator);
    }

    @Override
    protected NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                           Map<Player, TieBreakers> tieBreakers) {
        return this.calculator.innerCalculatePairings(state, rankPlayers(state.getPlayers(), tieBreakers));
    }

    public static int getDefaultNumberOfRounds(int numberOfPlayers) {
    	if (numberOfPlayers == 1) {
    		return 1;
    	}
        int closestSmallerPowerOfTwo = Integer.highestOneBit(numberOfPlayers);
        int minNumberOfRounds = 1;
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
     */
    public static LinkedHashMap<Player, Integer> rankPlayers(Collection<Player> allPlayers,
                                                              Map<Player, TieBreakers> tieBreakers) {
        LinkedHashMap<Player, Integer> rankings = Maps.newLinkedHashMap();
        List<Player> players = Lists.newArrayList(allPlayers);
        players.sort(Comparator.comparing(a -> tieBreakers.getOrDefault(a, TieBreakers.BYE)));
        for (int i = 0; i < players.size(); i++) {
            rankings.put(players.get(i), i);
        }
        return rankings;
    }
}
