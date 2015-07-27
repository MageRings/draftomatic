package magic.swiss;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

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

import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
public class SwissTournament {

    private final ConcurrentNavigableMap<Integer, Map<Player, Result>> results = new ConcurrentSkipListMap<>();
    private final Map<Integer, Player> players = Maps.newHashMap();

    public SwissTournament(Collection<Player> players) {
        for (Player p : players) {
            if (players.contains(p.getId())) {
                throw new IllegalArgumentException("Two players have the same id: " + p.getId());
            }
            this.players.put(p.getId(), p);
        }
        if (this.players.size() % 2 != 0) {
            //add in a bye
            this.players.put(0, Player.BYE);
        }
    }

    public synchronized NavigableSet<Result> registerResults(int thisRound, Collection<Result> thisRoundResults) {
        if (results.size() == 0) {
            if (thisRound != 1) {
                throw new IllegalArgumentException("Still waiting on results from the first round!");
            }
        } else if (results.lastKey() > thisRound) {
            throw new IllegalArgumentException("We have already paired the next round!  Undo that round first");
        }
        Map<Player, Result> newResultEntry = Maps.newHashMap();
        for (Result r : thisRoundResults) {
            Result resultToRecord = r;
            //player 1 must be the bye since the bye has the lowest id
            if (r.getPairing().getPlayer1().equals(Player.BYE)) {
                resultToRecord = new Result(r.getPairing(), 0, 2, 0);
            }
            newResultEntry.put(resultToRecord.getPairing().getPlayer1(), resultToRecord);
            newResultEntry.put(resultToRecord.getPairing().getPlayer2(), resultToRecord);
        }
        results.put(thisRound, newResultEntry);
        return Sets.newTreeSet(results.get(thisRound).values());
    }

    Constraint cannotPlayAgain(IntVar player1, IntVar player2) {
        return ICF.arithm(player1, "!=", player2, "-", 1);
    }

    IntVar createPlayerVariable(Solver solver, int playerId, int rangeStart, int rangeEnd) {
        return VariableFactory.bounded(String.valueOf(playerId), rangeStart, rangeEnd - 1, solver);
    }

    public synchronized NavigableSet<Pairing> getPairings(int round) {
        // check for previous cached pairings

        return calculatePairings();
    }

    private int getPointsForPlayer(Map<Player, Integer> pointsPerPlayer, Result result, Player player) {
        int points = 0;
        if (pointsPerPlayer.containsKey(player)) {
            points = pointsPerPlayer.get(player);
        }
        points += result.getPointsForPlayer(player);
        return points;
    }

    private synchronized NavigableSet<Pairing> calculatePairings() {
        Multimap<Player, Player> alreadyMatched = HashMultimap.create();
        Map<Player, Integer> pointsPerPlayer = Maps.newHashMap();
        for (Player p : players.values()) {
            pointsPerPlayer.put(p, 0);
        }
        for (Map<Player, Result> roundResults : results.values()) {
            Set<Result> resultsUsed = Sets.newHashSet();
            for (Result result : roundResults.values()) {
                Pairing pairing = result.getPairing();
                // one of these ids is the player. we don't really care
                alreadyMatched.put(pairing.getPlayer1(), pairing.getPlayer2());
                alreadyMatched.put(pairing.getPlayer2(), pairing.getPlayer1());
                if (!resultsUsed.contains(result)) {
                    pointsPerPlayer.put(pairing.getPlayer1(), getPointsForPlayer(pointsPerPlayer, result, pairing.getPlayer1()));
                    pointsPerPlayer.put(pairing.getPlayer2(), getPointsForPlayer(pointsPerPlayer, result, pairing.getPlayer2()));
                }
                resultsUsed.add(result);
            }
        }
        // NavigableMap<Integer, Integer> numberOfPlayersAtEachPointLevel =
        // getNumberOfPlayersAtEachPointLevel(pointsPerPlayer);
        Multimap<Integer, Player> playersAtEachPointLevel = Multimaps.invertFrom(Multimaps.forMap(pointsPerPlayer), HashMultimap.<Integer, Player> create());
        Solver solver = new Solver();
        Map<Player, IntVar> playerVariables = createPlayerVariables(solver, playersAtEachPointLevel);
        disallowRepairing(solver, playerVariables, alreadyMatched);
        solver.post(ICF.alldifferent(playerVariables.values().toArray(new IntVar[0])));

        solver.findSolution();

        NavigableSet<Pairing> pairings = solutionToPairings(playerVariables, pointsPerPlayer);
        return pairings;
    }

    // sort pairings by combined points of two players
    private NavigableSet<Pairing> solutionToPairings(Map<Player, IntVar> playerVariables, Map<Player, Integer> pointsPerPlayer) {
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

    private void disallowRepairing(Solver solver, Map<Player, IntVar> playerVariables, Multimap<Player, Player> alreadyMatched) {
        for (Map.Entry<Player, IntVar> playerAndVariable : playerVariables.entrySet()) {
            for (Player other : alreadyMatched.get(playerAndVariable.getKey())) {
                Constraint constraint = cannotPlayAgain(playerAndVariable.getValue(), playerVariables.get(other));
                solver.post(constraint);
            }
        }
    }

    private Map<Player, IntVar> createPlayerVariables(Solver solver, Multimap<Integer, Player> playersAtEachPointLevel) {
        Map<Player, IntVar> result = Maps.newHashMap();
        int rangeStart = 0;
        for (Integer pointLevel : playersAtEachPointLevel.keySet()) {
            Collection<Player> players = playersAtEachPointLevel.get(pointLevel);
            result.putAll(createOneTierOfPlayers(solver, rangeStart, rangeStart + players.size(), players));
            rangeStart += players.size();
        }
        return result;
    }

    private Map<Player, IntVar> createOneTierOfPlayers(Solver solver, int rangeStart, int rangeEnd, Collection<Player> playersInTier) {
        // must be shuffled to ensure a random result each time this is run
        List<Player> shuffledPlayers = Lists.newArrayList(playersInTier); // TODO: handle drops
        Collections.shuffle(shuffledPlayers);
        Map<Player, IntVar> result = Maps.newHashMap();
        for (int i = 0; i < shuffledPlayers.size(); i++) {
            result.put(shuffledPlayers.get(i), createPlayerVariable(solver, i, rangeStart, rangeEnd));
        }
        return result;
    }
}
