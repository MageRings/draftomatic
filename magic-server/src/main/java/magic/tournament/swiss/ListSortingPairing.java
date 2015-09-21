package magic.tournament.swiss;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import magic.data.Pairing;
import magic.data.Player;

public class ListSortingPairing implements SwissPairingCalculator {

    public ListSortingPairing() {
    }

    private static void disallowRepairing(Solver solver,
                                          Map<Player, IntVar> playerVariables,
                                          Multimap<Player, Player> alreadyMatched) {
        for (Map.Entry<Player, IntVar> playerAndVariable : playerVariables.entrySet()) {
            for (Player other : alreadyMatched.get(playerAndVariable.getKey())) {
                Constraint constraint = cannotPlayAgain(playerAndVariable.getValue(), playerVariables.get(other));
                solver.post(constraint);
            }
        }
    }

    private static Map<Player, IntVar> createPlayerVariables(Solver solver,
                                                             Multimap<Integer, Player> playersAtEachPointLevel,
                                                             Map<Player, Integer> playerRankings) {
        Map<Player, IntVar> result = Maps.newHashMap();
        int rangeStart = 0;
        for (Integer pointLevel : playersAtEachPointLevel.keySet()) {
            Collection<Player> players = playersAtEachPointLevel.get(pointLevel);
            result.putAll(
                    createOneTierOfPlayers(solver, rangeStart, rangeStart + players.size(), players, playerRankings));
            rangeStart += players.size();
        }
        return result;
    }

    private static Map<Player, IntVar> createOneTierOfPlayers(Solver solver,
                                                              int rangeStart,
                                                              int rangeEnd,
                                                              Collection<Player> playersInTier,
                                                              Map<Player, Integer> playerRankings) {
        List<Player> rankedPlayers = Lists.newArrayList(playersInTier);
        Collections.sort(rankedPlayers, (a, b) -> playerRankings.get(a).compareTo(playerRankings.get(b)));
        Map<Player, IntVar> result = Maps.newHashMap();
        for (int i = 0; i < rankedPlayers.size(); i++) {
            result.put(rankedPlayers.get(i), createPlayerVariable(solver, i, rangeStart, rangeEnd));
        }
        return result;
    }

    // sort pairings by combined points of two players
    private static NavigableSet<Pairing> solutionToPairings(Map<Player, IntVar> playerVariables,
                                                            Map<Player, Integer> pointsPerPlayer) {
        Player[] sortedPlayers = new Player[playerVariables.size()];
        for (Map.Entry<Player, IntVar> entry : playerVariables.entrySet()) {
            sortedPlayers[entry.getValue().getValue()] = entry.getKey();
        }
        // TODO: list must be even
        NavigableSet<Pairing> pairings = Sets.newTreeSet();
        for (int i = 0; i < sortedPlayers.length; i += 2) {
            Player player1 = sortedPlayers[i];
            Player player2 = sortedPlayers[i + 1];
            int totalPoints = pointsPerPlayer.get(player1) + pointsPerPlayer.get(player2);
            pairings.add(new Pairing(player1, player2, totalPoints));
        }
        return pairings;
    }

    private static Constraint cannotPlayAgain(IntVar player1, IntVar player2) {
        return ICF.arithm(player1, "!=", player2, "-", 1);
    }

    private static IntVar createPlayerVariable(Solver solver, int playerId, int rangeStart, int rangeEnd) {
        return VariableFactory.bounded(String.valueOf(playerId), rangeStart, rangeEnd - 1, solver);
    }

    @Override
    public NavigableSet<Pairing> innerCalculatePairings(
                                                        Multimap<Integer, Player> playersAtEachPointLevel,
                                                        Map<Player, Integer> playerRankings,
                                                        Map<Player, Integer> pointsPerPlayer,
                                                        Multimap<Player, Player> alreadyMatched) {
        Solver solver = new Solver();
        Map<Player, IntVar> playerVariables = createPlayerVariables(solver, playersAtEachPointLevel, playerRankings);
        disallowRepairing(solver, playerVariables, alreadyMatched);
        solver.post(ICF.alldifferent(playerVariables.values().toArray(new IntVar[0])));
        if (!solver.findSolution()) {
            throw new IllegalStateException("Could not find pairings!");
        }
        return solutionToPairings(playerVariables, pointsPerPlayer);
    }
}
