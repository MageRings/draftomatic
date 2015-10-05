package magic.tournament;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.data.Round;
import magic.data.TournamentStatus;
import magic.tournament.swiss.TournamentState;

public abstract class AbstractTournament implements Tournament {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // this block represents the entire persisted state
    // these elements are immutable
    private final String tournamentId;
    private final Set<Player> allPlayers;
    private final int numberOfRounds;
    // these elements are mutable
    private final NavigableSet<Round> overallResults = Sets.newTreeSet();
    private boolean isComplete = false;

    public AbstractTournament(String tournamentId, int numberOfRounds, Collection<Player> inputPlayers) {
        this.tournamentId = tournamentId;
        Set<Player> players = Sets.newHashSet();
        for (Player p : inputPlayers) {
            if (players.contains(p.getId())) {
                throw new IllegalArgumentException("Two players have the same id: " + p.getId());
            }
            players.add(p);
        }
        this.allPlayers = players;
        this.numberOfRounds = numberOfRounds;
        getPairingsAndAddToResults(TournamentState.createTournamentState(players, Sets.newTreeSet()));
    }

    protected String getTournamentId() {
        return tournamentId;
    }

    protected int getCurrentRound() {
        lock.readLock().lock();
        try {
            return overallResults.last().getNumber();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public TournamentStatus getStatus() {
        lock.readLock().lock();
        try {
            return new TournamentStatus(
                    overallResults.last().getNumber(),
                    numberOfRounds,
                    isComplete,
                    overallResults,
                    Sets.newTreeSet(TieBreakers.getTieBreakers(
                            overallResults,
                            tournamentId).values()));
        } finally {
            lock.readLock().unlock();
        }
    }

    private int roundToUse(Optional<Integer> roundRequested) {
        int round = overallResults.last().getNumber();
        if (roundRequested.isPresent()) {
            round = roundRequested.get();
        }
        if (round > numberOfRounds) {
            throw new IllegalArgumentException("This tournament only has " + numberOfRounds + " rounds!");
        }
        return round;
    }

    /**
     *
     * @param roundRequested
     * @param thisRoundResults
     * @return - the next round (without results) or null if the tournament is complete
     */
    @Override
    public Round registerResults(Optional<Integer> roundRequested, Collection<Match> thisRoundResults) {
        lock.writeLock().lock();
        try {
            if (isComplete) {
                throw new IllegalArgumentException("This tournament is already compelete!");
            }
            int round = roundToUse(roundRequested);
            int currentRound = overallResults.last().getNumber();
            if (round != currentRound) {
                throw new IllegalArgumentException(
                        "You may only enter results for the current round (" + currentRound + ")!");
            }
            // remove the temporary pairings for the round
            // TODO: verify that the pairings passed in are legit
            overallResults.pollLast();
            overallResults.add(new Round(round, Sets.newTreeSet(thisRoundResults), true));
            if (currentRound == numberOfRounds) {
                isComplete = true;
                return null;
            }
            currentRound += 1;
            return new Round(
                    currentRound,
                    getPairingsAndAddToResults(TournamentState.createTournamentState(allPlayers, overallResults)),
                    false);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private NavigableSet<Match> getPairingsAndAddToResults(TournamentState state) {
        int currentRound = overallResults.last().getNumber();
        NavigableSet<Pairing> pairings = calculatePairings(state, currentRound == numberOfRounds);
        NavigableSet<Match> matches = Sets.newTreeSet(
                pairings.stream().map(pairing -> new Match(pairing, Result.INCOMPLETE, false, false)).collect(
                        Collectors.toSet()));
        Map<Player, Match> tmp = Maps.newHashMap();
        for (Match m : matches) {
            tmp.put(m.getPairing().getPlayer1(), m);
            tmp.put(m.getPairing().getPlayer2(), m);
        }
        overallResults.add(new Round(currentRound, matches, false));
        return matches;
    }

    private NavigableSet<Pairing> calculatePairings(TournamentState state, boolean lastRound) {
        Optional<Map<Player, TieBreakers>> tieBreakers = Optional.empty();
        // edge case for one round tournament
        // TODO: factor this out by making tiebreaker code more robust
        if (lastRound && overallResults.size() > 0) {
            tieBreakers = Optional.of(TieBreakers.getTieBreakers(overallResults, tournamentId));
        }
        return innerCalculatePairings(state, tieBreakers);
    }

    protected abstract NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                                    Optional<Map<Player, TieBreakers>> tieBreakers);

    @Override
    public NavigableSet<TieBreakers> getTieBreakers(Optional<Integer> roundRequested) {
        lock.readLock().lock();
        try {
            int round = roundToUse(roundRequested);
            Collection<Round> truncatedResults =
                    overallResults.stream().filter(r -> r.getNumber() <= round).collect(Collectors.toSet());
            return Sets.newTreeSet(TieBreakers.getTieBreakers(truncatedResults, tournamentId).values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + numberOfRounds;
        result = prime * result + ((overallResults == null) ? 0 : overallResults.hashCode());
        result = prime * result + ((allPlayers == null) ? 0 : allPlayers.hashCode());
        result = prime * result + ((tournamentId == null) ? 0 : tournamentId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractTournament other = (AbstractTournament) obj;
        if (numberOfRounds != other.numberOfRounds) {
            return false;
        }
        if (overallResults == null) {
            if (other.overallResults != null) {
                return false;
            }
        } else if (!overallResults.equals(other.overallResults)) {
            return false;
        }
        if (allPlayers == null) {
            if (other.allPlayers != null) {
                return false;
            }
        } else if (!allPlayers.equals(other.allPlayers)) {
            return false;
        }
        if (tournamentId == null) {
            if (other.tournamentId != null) {
                return false;
            }
        } else if (!tournamentId.equals(other.tournamentId)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Solver solver = new Solver();
        IntVar cost = VariableFactory.bounded("cost", 0, 100000, solver);
        int nodes = 10;
        int[][] costs = new int[nodes][nodes];
        UndirectedGraph graphLowerBound = new UndirectedGraph(solver, nodes, SetType.LINKED_LIST, true);
        UndirectedGraph graphUpperBound = new UndirectedGraph(solver, nodes, SetType.BIPARTITESET, true);
        // every node will have degree 1
        for (int i = 0; i < nodes; i++) {
            for (int j = i; j < nodes; j++) {
                graphUpperBound.addEdge(i, j);
                costs[i][j] = i;
                costs[j][i] = i;
            }
        }
        IUndirectedGraphVar graph = GraphVarFactory.undirected_graph_var("g", graphLowerBound, graphUpperBound, solver);
        IntVar[] degrees = VariableFactory.integerArray("degrees", nodes, 1, 1, solver);
        System.out.println(degrees);
        System.out.println(degrees[0].getLB());
        System.out.println(degrees[0].getUB());

        // solver.post(GraphConstraintFactory.degrees(graph, degrees));
        // solver.post(new Constraint("Edge-cost", new Propagator[]{new PropTreeCostSimple(graph,
        // cost, costs)}));
        // solver.post(GraphConstraintFactory.tsp(graph, cost, costs, 0));
        Propagator[] props = new Propagator[] {
                // new EdgeWeightConstraint(graph, cost, costs, nodes),
                new PropNodeDegree_Var(graph, degrees),
                new PropTreeCostSimple(graph, cost, costs),
        };
        solver.post(new Constraint("Edge-cost", props));
        // solver.post(new Constraint("Edge-cost", new Propagator[]{new EdgeWeightConstraint(graph,
        // cost, costs, nodes)}));
        solver.set(GraphStrategyFactory.lexico(graph));
        Chatterbox.showSolutions(solver);
        // solver.findSolution();
        solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, cost);
        System.out.println("Is feasible: " + solver.isFeasible());
        System.out.println("Is satisfied: " + solver.isSatisfied());
        Chatterbox.printStatistics(solver);
        System.out.println(graph.isInstantiated());
        System.out.println(cost.isInstantiated());
        System.out.println(cost);
        System.out.println(graph.getPotNeighOf(1));
    }
    /*
     * public static void main(String[] args) { Solver solver = new Solver(); IntVar cost =
     * VariableFactory.integer("cost", 0, 1000, solver); IntVar[] costs =
     * VariableFactory.integerArray("costs", 2, 0, 1000, solver); SetVar set =
     * VariableFactory.set("test", new int[]{10,8}, solver); SetConstraintsFactory.int_channel(new
     * SetVar[]{set}, costs, 0, 0); IntConstraintFactory.sum(new IntVar[]{VariableFactory.fixed(7,
     * solver), VariableFactory.fixed(3, solver)}, cost); //SetConstraintsFactory.sum(set, cost,
     * true); //SetConstraintsFactory.sum(set, new int[]{7,4}, 0, cost, true);
     * //solver.post(SetConstraintsFactory.cardinality(set, VariableFactory.fixed(1, solver)));
     * //solver.post(new Constraint("Edge-cost", new Propagator[]{new EdgeWeightConstraint(graph,
     * cost, costs, nodes)})); Chatterbox.showSolutions(solver); //solver.findSolution();
     * solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, cost); System.out.println(
     * "Is feasible: " + solver.isFeasible()); System.out.println("Is satisfied: " +
     * solver.isSatisfied()); Chatterbox.printStatistics(solver); }
     */
}
