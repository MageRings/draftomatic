package magic.tournament.swiss;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.cstrs.cost.trees.PropTreeCostSimple;
import org.chocosolver.solver.cstrs.degree.PropNodeDegree_Var;
import org.chocosolver.solver.search.GraphStrategyFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.GraphVarFactory;
import org.chocosolver.solver.variables.IUndirectedGraphVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;

import com.google.common.collect.Multimap;
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
    private static int[][] determineEdgeWeights(Multimap<Integer, Player> playersAtEachPointLevel,
                                                NavigableMap<Player, Integer> playerRankings,
                                                Multimap<Player, Player> alreadyMatched) {
        int[][] weights = new int[playerRankings.size()][playerRankings.size()];
        Player[] players = new Player[playerRankings.size()];
        playerRankings.navigableKeySet().toArray(players);
        NavigableMap<Integer, Integer> numberOfPlayersPerPointTier = playersAtEachPointLevel.asMap()
                .entrySet()
                .stream()
                .collect(MapCollectorUtils.toNavMap(e -> e.getKey(), e -> e.getValue().size()));
        Map<Player, Integer> pointsPerPlayer = playersAtEachPointLevel.asMap()
                .entrySet()
                .stream()
                .collect(
                        HashMap::new,
                        (map, pointAndPlayers) -> {
                            pointAndPlayers.getValue()
                                    .stream()
                                    .forEach(player -> map.put(player, pointAndPlayers.getKey()));
                            return;
                        } ,
                        Map::putAll);
        for (int i = 0; i < playerRankings.size(); i++) {
            // we don't assign a cost for self-pairings because these are not possible anyways
            for (int j = i + 1; j < playerRankings.size(); j++) {
                int weight = determineSingleEdgeWeight(
                        players[i],
                        players[j],
                        numberOfPlayersPerPointTier,
                        pointsPerPlayer,
                        playerRankings,
                        alreadyMatched);
                weights[i][j] = weight;
                weights[j][i] = weight;
            }
        }
        return weights;
    }

    // TODO(btoth): have to watch out for overflow here with larger tournaments
    private static int determineSingleEdgeWeight(Player player1,
                                                 Player player2,
                                                 NavigableMap<Integer, Integer> numberOfPlayersPerPointTier,
                                                 Map<Player, Integer> pointsPerPlayer,
                                                 Map<Player, Integer> playerRankings,
                                                 Multimap<Player, Player> alreadyMatched) {
        int weight = 0;
        if (alreadyMatched.containsEntry(player1, player2)) {
            // first check to see if they were already matched
            weight = 2 * playerRankings.keySet().size();
        }
        // then check add points for each tier apart
        int p1Points = pointsPerPlayer.get(player1);
        int p2Points = pointsPerPlayer.get(player2);
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

    @Override
    public NavigableSet<Pairing> innerCalculatePairings(Multimap<Integer, Player> playersAtEachPointLevel,
                                                        NavigableMap<Player, Integer> playerRankings,
                                                        Map<Player, Integer> pointsPerPlayer,
                                                        Multimap<Player, Player> alreadyMatched) {
        NavigableSet<Pairing> result = Sets.newTreeSet();
        int[][] edgeWeights = determineEdgeWeights(playersAtEachPointLevel, playerRankings, alreadyMatched);
        int numPlayers = playerRankings.size();
        Solver solver = new Solver();
        IntVar cost = VariableFactory.bounded("cost", 0, Integer.MAX_VALUE - 1, solver);
        UndirectedGraph graphLowerBound = new UndirectedGraph(solver, numPlayers, SetType.LINKED_LIST, true);
        UndirectedGraph graphUpperBound = new UndirectedGraph(solver, numPlayers, SetType.BIPARTITESET, true);
        for (int i = 0; i < numPlayers; i++) {
            for (int j = i + 1; j < numPlayers; j++) {
                graphUpperBound.addEdge(i, j);
            }
        }
        IUndirectedGraphVar graph = GraphVarFactory.undirected_graph_var("g", graphLowerBound, graphUpperBound, solver);
        IntVar[] degrees = VariableFactory.integerArray("degrees", numPlayers, 1, 1, solver);

        Propagator[] props = new Propagator[] {
                // new EdgeWeightConstraint(graph, cost, costs, nodes),
                new PropNodeDegree_Var(graph, degrees),
                new PropTreeCostSimple(graph, cost, edgeWeights),
        };
        solver.post(new Constraint("Edge-cost", props));
        solver.set(GraphStrategyFactory.lexico(graph));
        Chatterbox.showSolutions(solver);
        solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, cost);
    }

}
