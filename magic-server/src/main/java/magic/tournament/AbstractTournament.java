package magic.tournament;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

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
            overallResults.add(new Round(round, true, Sets.newTreeSet(thisRoundResults)));
            if (currentRound == numberOfRounds) {
                isComplete = true;
                return null;
            }
            currentRound += 1;
            return new Round(
                    currentRound,
                    false,
                    getPairingsAndAddToResults(TournamentState.createTournamentState(allPlayers, overallResults)));
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
        overallResults.add(new Round(currentRound, false, matches));
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
}
