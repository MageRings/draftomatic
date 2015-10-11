package magic.tournament.swiss;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.cstrs.cost.trees.PropTreeCostSimple;
import org.chocosolver.solver.cstrs.degree.PropNodeDegree_Var;
import org.chocosolver.solver.search.GraphStrategyFactory;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.variables.GraphVarFactory;
import org.chocosolver.solver.variables.IUndirectedGraphVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.ISet;
import org.chocosolver.util.objects.setDataStructures.SetType;

import com.google.common.collect.Sets;

import magic.data.Pairing;
import magic.data.Player;

public class GraphPairing implements SwissPairingCalculator {

    public GraphPairing() {
    }

    /**
     * Creates a system of edge weights as follows: 1. Within a tier, players are sorted randomly
     * (based on current round and tournament ID). The cost to pair against a player is equal to the
     * number of spaces away. This serves as a small delta to ensure consistent pairings. 2. Pairing
     * to the next tier is equal to the number in the current tier plus a delta. The delta is
     * determined as above. 3. Pairing to a player already played against is equal to the number of
     * players plus the tier difference plus the usual delta.
     *
     * @return
     */
    private static int[][] determineEdgeWeights(TournamentState state, LinkedHashMap<Player, Integer> playerRankings) {
        int[][] weights = new int[state.getNumberOfPlayers()][state.getNumberOfPlayers()];
        Player[] players = playerRankings.keySet().toArray(new Player[] {});
        for (int i = 0; i < players.length; i++) {
            // we don't assign a cost for self-pairings because these are not possible anyways
            for (int j = i + 1; j < players.length; j++) {
                int weight = determineSingleEdgeWeight(players[i], players[j], state, playerRankings);
                weights[i][j] = weight;
                weights[j][i] = weight;
            }
        }
        return weights;
    }

    // TODO(btoth): have to watch out for overflow here with larger tournaments
    private static int determineSingleEdgeWeight(Player player1,
                                                 Player player2,
                                                 TournamentState state,
                                                 LinkedHashMap<Player, Integer> playerRankings) {
        int weight = 0;
        if (state.getRoundData().get(player1).getAlreadyMatched().contains(player2)) {
            // first check to see if they were already matched
            weight = 2 * state.getNumberOfPlayers();
        }
        // then add points equal to the size of each tier separating the players
        int p1Points = state.getRoundData().get(player1).getPoints();
        int p2Points = state.getRoundData().get(player2).getPoints();
        NavigableMap<Integer, Integer> numberOfPlayersPerPointTier =
                state.getPlayersAtEachPointLevel()
                        .entrySet()
                        .stream()
                        .collect(MapCollectorUtils.toNavMap(Entry::getKey, e -> e.getValue().size()));
        NavigableMap<Integer, Integer> tiersToConsider;
        if (p1Points > p2Points) {
            tiersToConsider = (NavigableMap<Integer, Integer>) numberOfPlayersPerPointTier.subMap(p2Points, p1Points);
        } else {
            tiersToConsider = (NavigableMap<Integer, Integer>) numberOfPlayersPerPointTier.subMap(p1Points, p2Points);
        }
        for (Integer numPlayers : tiersToConsider.values()) {
            weight += numPlayers;
        }
        // always add a delta to ensure consistent results
        weight += Math.abs(playerRankings.get(player1) - playerRankings.get(player2));
        return weight;
    }

    private static NavigableSet<Pairing> hydratePairingsFromResult(IUndirectedGraphVar g,
                                                                   TournamentState state,
                                                                   Map<Player, Integer> playerRankings) {
        NavigableSet<Pairing> result = Sets.newTreeSet();
        Set<Player> playersPaired = Sets.newHashSet();
        ISet nodes = g.getMandatoryNodes();
        Player[] players = playerRankings.keySet().toArray(new Player[] {});
        for (int i = nodes.getFirstElement(); i >= 0; i = nodes.getNextElement()) {
            Player player1 = players[i];
            if (playersPaired.contains(player1)) {
                continue;
            }
            playersPaired.add(player1);
            Player player2 = players[g.getMandNeighOf(i).getFirstElement()];
            int totalPoints =
                    state.getRoundData().get(player1).getPoints() + state.getRoundData().get(player2).getPoints();
            result.add(new Pairing(player1, player2, totalPoints));
        }
        return result;
    }

    @Override
    public NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                        LinkedHashMap<Player, Integer> playerRankings) {
        int[][] edgeWeights = determineEdgeWeights(state, playerRankings);
        int numPlayers = state.getNumberOfPlayers();
        Solver solver = new Solver();
        IntVar cost = VariableFactory.bounded("cost", 0, VariableFactory.MAX_INT_BOUND, solver);
        UndirectedGraph graphLowerBound = new UndirectedGraph(solver, numPlayers, SetType.LINKED_LIST, true);
        UndirectedGraph graphUpperBound = new UndirectedGraph(solver, numPlayers, SetType.BIPARTITESET, true);
        for (int i = 0; i < numPlayers; i++) {
            for (int j = i + 1; j < numPlayers; j++) {
                // avoid adding self-edges
                graphUpperBound.addEdge(i, j);
            }
        }
        IUndirectedGraphVar graph = GraphVarFactory.undirected_graph_var("g", graphLowerBound, graphUpperBound, solver);
        IntVar[] degrees = VariableFactory.integerArray("degrees", numPlayers, 1, 1, solver);

        @SuppressWarnings("rawtypes")
        Propagator[] props = new Propagator[] {
                new PropNodeDegree_Var(graph, degrees),
                new PropTreeCostSimple(graph, cost, edgeWeights),
        };
        solver.post(new Constraint("Edge-cost", props));
        solver.set(GraphStrategyFactory.lexico(graph));
        final AtomicReference<NavigableSet<Pairing>> variables = new AtomicReference<>();
        solver.plugMonitor(
                (IMonitorSolution) () -> {
                    IUndirectedGraphVar v = (IUndirectedGraphVar) solver.getStrategy().getVariables()[0];
                    variables.set(hydratePairingsFromResult(v, state, playerRankings));
                });
        solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, cost);
        return variables.get();
    }
}
