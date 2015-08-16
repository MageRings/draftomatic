package magic.swiss;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.data.Round;
import magic.data.TournamentStatus;

public class SwissTournament {

    private final String tournamentId;
    private final ConcurrentNavigableMap<Integer, Map<Player, Match>> overallResults = new ConcurrentSkipListMap<>();
    private final ConcurrentMap<Long, Player> players = Maps.newConcurrentMap();
    private int currentRound = 1;
    private final int numberOfRounds;
    private boolean isComplete = false;

    public SwissTournament(String tournamentId, int numberOfRounds, Collection<Player> players) {
        this.tournamentId = tournamentId;
        for (Player p : players) {
            if (players.contains(p.getId())) {
                throw new IllegalArgumentException("Two players have the same id: " + p.getId());
            }
            this.players.put(p.getId(), p);
        }
        if (this.players.size() % 2 != 0) {
            // add in a bye
            this.players.put(0L, Player.BYE);
        }
        this.numberOfRounds = numberOfRounds;
        getPairings();
    }

    private Round getRound(int roundNum) {
        NavigableSet<Match> matches =  Sets.newTreeSet(overallResults.get(roundNum).values());
        return new Round(roundNum, matches, isComplete || roundNum != currentRound);
    }

    public TournamentStatus getStatus() {
        NavigableSet<Round> rounds = Sets.newTreeSet();
        for (int i = 1; i <= currentRound; i++) {
            rounds.add(getRound(i));
        }
        return new TournamentStatus(currentRound, numberOfRounds, isComplete, rounds,
                Sets.newTreeSet(TieBreakers.getTieBreakers(overallResults.values(),
                        calculatePointsPerPlayer(overallResults.values()),
                        tournamentId).values()));
    }

    private int roundToUse(Optional<Integer> roundRequested) {
        int round = currentRound;
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
     * @return - the next round (without results)
     */
    public synchronized Round registerResults(Optional<Integer> roundRequested, Collection<Match> thisRoundResults) {
        if (isComplete) {
            throw new IllegalArgumentException("This tournament is already compelete!");
        }
        int round = roundToUse(roundRequested);
        if (overallResults.size() == 0) {
            if (round != 1) {
                throw new IllegalArgumentException("Still waiting on results from the first round!");
            }
        } else if (overallResults.lastKey() > round) {
            throw new IllegalArgumentException("We have already paired the next round!  Undo that round first");
        }
        Map<Player, Match> newResultEntry = Maps.newHashMap();
        for (Match m : thisRoundResults) {
            Match resultToRecord = m;
            if (m.getPairing().isBye()) {
                resultToRecord = new Match(m.getPairing(), new Result(0, 2, 0));
            }
            newResultEntry.put(m.getPairing().getPlayer1(), resultToRecord);
            newResultEntry.put(m.getPairing().getPlayer2(), resultToRecord);
        }
        overallResults.put(round, newResultEntry);
        // if we have received results for the current round then we can advance the tournament
        if (round == currentRound) {
            if (currentRound == numberOfRounds) {
                isComplete = true;
            } else {
                currentRound += 1;
                return new Round(currentRound, getPairings(), false);
            }
        }
        return new Round(currentRound, Sets.newTreeSet(newResultEntry.values()), true);
    }

    Constraint cannotPlayAgain(IntVar player1, IntVar player2) {
        return ICF.arithm(player1, "!=", player2, "-", 1);
    }

    IntVar createPlayerVariable(Solver solver, int playerId, int rangeStart, int rangeEnd) {
        return VariableFactory.bounded(String.valueOf(playerId), rangeStart, rangeEnd - 1, solver);
    }

    private synchronized NavigableSet<Match> getPairings() {
        NavigableSet<Pairing> pairings = calculatePairings(overallResults.values(), currentRound == numberOfRounds);
        NavigableSet<Match> matches = Sets.newTreeSet(pairings.stream().map(pairing -> new Match(pairing, Result.INCOMPLETE)).collect(Collectors.toSet()));
        Map<Player, Match> tmp = Maps.newHashMap();
        for (Match m : matches) {
            tmp.put(m.getPairing().getPlayer1(), m);
            tmp.put(m.getPairing().getPlayer2(), m);
        }
        overallResults.put(currentRound, tmp);
        return matches;
    }

    private Map<Player, Integer> calculatePointsPerPlayer(Collection<Map<Player, Match>> results) {
        return results.stream().collect(HashMap::new,
                (map, resultPerPlayer) -> resultPerPlayer.entrySet().stream()
                .forEach(entry -> map.put(entry.getKey(), entry.getValue().getPointsForPlayer(entry.getKey()))),
                (finalMap, map) -> map.forEach((k, v) -> finalMap.merge(k, v, Integer::sum)));
    }

    private Multimap<Player, Player> alreadyMatched(Collection<Map<Player, Match>> results) {
        return results.stream().collect(HashMultimap::create,
                (map, resultPerPlayer) -> resultPerPlayer.entrySet().stream().forEach(entry ->
                {
                    Player player = entry.getKey();
                    map.put(player, entry.getValue().getPairing().getOpponent(player));
                }),
                (finalMap, map) -> map.asMap().forEach(finalMap::putAll));
    }

    private NavigableSet<Pairing> calculatePairings(Collection<Map<Player, Match>> results, boolean lastRound) {
        Multimap<Player, Player> alreadyMatched = alreadyMatched(results);
        Map<Player, Integer> pointsPerPlayer = calculatePointsPerPlayer(results);
        if (pointsPerPlayer.isEmpty()) {
            pointsPerPlayer = players.values().stream().collect(Collectors.toMap(Function.identity(), player -> 0));
        }
        Multimap<Integer, Player> playersAtEachPointLevel = Multimaps.invertFrom(Multimaps.forMap(pointsPerPlayer),
                HashMultimap.<Integer, Player> create());
        Optional<Map<Player, TieBreakers>> tieBreakers = Optional.empty();
        if (lastRound) {
            tieBreakers = Optional.of(TieBreakers.getTieBreakers(results, pointsPerPlayer, tournamentId));
        }
        Solver solver = new Solver();
        Map<Player, IntVar> playerVariables = createPlayerVariables(solver, playersAtEachPointLevel, tieBreakers);
        disallowRepairing(solver, playerVariables, alreadyMatched);
        solver.post(ICF.alldifferent(playerVariables.values().toArray(new IntVar[0])));

        if (!solver.findSolution()) {
            throw new IllegalStateException("Could not find pairings!");
        }

        NavigableSet<Pairing> pairings = solutionToPairings(playerVariables, pointsPerPlayer);
        return pairings;
    }

    // sort pairings by combined points of two players
    private NavigableSet<Pairing> solutionToPairings(Map<Player, IntVar> playerVariables, Map<Player, Integer> pointsPerPlayer) {
        Player[] sortedPlayers = new Player[playerVariables.size()];
        for (Map.Entry<Player, IntVar> entry : playerVariables.entrySet()) {
            System.out.println(entry.getValue().getValue());
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

    private void disallowRepairing(Solver solver, Map<Player, IntVar> playerVariables, Multimap<Player, Player> alreadyMatched) {
        for (Map.Entry<Player, IntVar> playerAndVariable : playerVariables.entrySet()) {
            for (Player other : alreadyMatched.get(playerAndVariable.getKey())) {
                Constraint constraint = cannotPlayAgain(playerAndVariable.getValue(), playerVariables.get(other));
                solver.post(constraint);
            }
        }
    }

    private Map<Player, IntVar> createPlayerVariables(Solver solver, Multimap<Integer, Player> playersAtEachPointLevel,
                                                      Optional<Map<Player, TieBreakers>> tieBreakers) {
        Map<Player, IntVar> result = Maps.newHashMap();
        int rangeStart = 0;
        for (Integer pointLevel : playersAtEachPointLevel.keySet()) {
            Collection<Player> players = playersAtEachPointLevel.get(pointLevel);
            result.putAll(createOneTierOfPlayers(solver, rangeStart, rangeStart + players.size(), players, tieBreakers));
            rangeStart += players.size();
        }
        return result;
    }

    private Map<Player, IntVar> createOneTierOfPlayers(Solver solver, int rangeStart, int rangeEnd,
                                                       Collection<Player> playersInTier, Optional<Map<Player, TieBreakers>> tieBreakers) {
        // must be shuffled to ensure a random result each time this is run unless we are on the last round
        List<Player> shuffledPlayers = Lists.newArrayList(playersInTier);
        if (tieBreakers.isPresent()) {
            Collections.sort(shuffledPlayers, (a , b) -> tieBreakers.get().get(a).compareTo(tieBreakers.get().get(b)));
        } else {
            Collections.shuffle(shuffledPlayers);
        }
        Map<Player, IntVar> result = Maps.newHashMap();
        for (int i = 0; i < shuffledPlayers.size(); i++) {
            result.put(shuffledPlayers.get(i), createPlayerVariable(solver, i, rangeStart, rangeEnd));
        }
        return result;
    }

    public NavigableSet<TieBreakers> getTieBreakers(Optional<Integer> roundRequested) {
        int round = roundToUse(roundRequested);
        Collection<Map<Player, Match>> truncatedResults = overallResults.headMap(round, true).values();
        Map<Player, Integer> pointsPerPlayer = calculatePointsPerPlayer(truncatedResults);
        TreeSet<TieBreakers> a = Sets.newTreeSet(TieBreakers.getTieBreakers(truncatedResults, pointsPerPlayer, tournamentId).values());
        return a;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + numberOfRounds;
        result = prime * result + ((overallResults == null) ? 0 : overallResults.hashCode());
        result = prime * result + ((players == null) ? 0 : players.hashCode());
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
        SwissTournament other = (SwissTournament) obj;
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
        if (players == null) {
            if (other.players != null) {
                return false;
            }
        } else if (!players.equals(other.players)) {
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
}
