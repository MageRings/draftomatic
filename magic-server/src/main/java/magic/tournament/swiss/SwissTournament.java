package magic.tournament.swiss;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;

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
        super(
                db,
                new TournamentData(
                        tournamentId,
                        numberOfRounds.isPresent() ? numberOfRounds.get()
                            : SwissTournament.getDefaultNumberOfRounds(input.getPlayers().size()),
                        input,
                        ZonedDateTime.now(),
                        null,
                        Sets.newTreeSet()));
        this.calculator = calculator;
    }

    @Override
    protected NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                           Map<Player, TieBreakers> tieBreakers) {
        return this.calculator.innerCalculatePairings(state, rankPlayers(state, tieBreakers));
    }

    public static int getDefaultNumberOfRounds(int numberOfPlayers) {
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
     * @param pointsPerPlayer
     * @param tieBreakers
     * @return
     */
    private static LinkedHashMap<Player, Integer> rankPlayers(TournamentState state,
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
