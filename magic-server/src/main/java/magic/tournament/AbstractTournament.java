package magic.tournament;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.data.Round;
import magic.data.database.Database;
import magic.data.tournament.TournamentData;
import magic.data.tournament.TournamentStatus;
import magic.tournament.swiss.TournamentState;

public abstract class AbstractTournament implements Tournament {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Database      db;

    // this block represents the entire persisted state
    private final TournamentData data;
    // end persisted state

    public AbstractTournament(Database db, TournamentData data) {
        this.db = db;
        this.data = data;
    }

    // should be called from within a lock
    private int getCurrentRound() {
        return this.data.getRounds().size();
    }

    // should be called from within a lock
    private boolean isComplete() {
        return this.data.getRounds().size() == this.data.getNumberOfRounds()
                && this.data.getRounds().last().isComplete();

    }

    @Override
    public void initFirstRound() {
        this.lock.writeLock().lock();
        try {
            getPairingsAndAddToResults(TournamentState.createTournamentState(
                    this.data.getInput().getPlayers(),
                    Sets.newTreeSet()));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public TournamentStatus getStatus() {
        this.lock.readLock().lock();
        try {
            return new TournamentStatus(
                    getCurrentRound(),
                    isComplete(),
                    this.data,
                    Sets.newTreeSet(TieBreakers.getTieBreakers(
                            this.data.getInput().getPlayers(),
                            this.data.getRounds(),
                            this.data.getId()).values()));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    // should be called from within a lock
    private int roundToUse(Optional<Integer> roundRequested) {
        int round = getCurrentRound();
        if (roundRequested.isPresent()) {
            round = roundRequested.get();
        }
        if (round < 0) {
            throw new IllegalArgumentException("The first round in a tournament is round 1.");
        }
        if (this.getCurrentRound() < round) {
            throw new IllegalArgumentException("The tournament is currently on round " + this.getCurrentRound() + ", but round " + round + " was requested.");
        }
        if (round > this.data.getNumberOfRounds()) {
            throw new IllegalArgumentException(
                    "This tournament only has " + this.data.getNumberOfRounds() + " rounds!");
        }
        return round;
    }

    private static void validateResult(Match match) {
        String tooManyWinsError = "Match " + match + " is invalid.  Matches are played until one player has two wins.";
        Result result = match.getResult();
        if (result.getP1Wins() > 2 || result.getP2Wins() > 2) {
            throw new IllegalArgumentException(tooManyWinsError);
        }
        if (result.getP1Wins() == 2 && result.getP2Wins() > 1) {
            throw new IllegalArgumentException(tooManyWinsError);
        }
        if (result.getP2Wins() == 2 && result.getP1Wins() > 1) {
            throw new IllegalArgumentException(tooManyWinsError);
        }
    }

    /**
     * undoes the last complete round (if an incomplete round is present it is also reset)
     */
    @Override
    public Round undoLastRound() {
        this.lock.writeLock().lock();
        try {
            Round currentRound = this.data.getRounds().last();
            if (!currentRound.isComplete() && this.data.getRounds().size() > 1) {
            	this.data.getRounds().pollLast();
            	currentRound = this.data.getRounds().last();
            }
            currentRound.setComplete(false);
            return currentRound;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     *
     * @param roundRequested
     * @param thisRoundResults
     * @return - the next round (without results) or null if the tournament is complete
     */
    @Override
    public Round registerResults(Optional<Integer> roundRequested, Collection<Match> thisRoundResults) {
        this.lock.writeLock().lock();
        try {
            if (isComplete()) {
                throw new IllegalArgumentException("This tournament is already compelete!");
            }
            int round = roundToUse(roundRequested);
            int currentRound = this.data.getRounds().last().getNumber();
            if (round != currentRound) {
                throw new IllegalArgumentException(
                        "You may only enter results for the current round (" + currentRound + ")!");
            }
            // remove the temporary pairings for the round
            Round expectedRound = this.data.getRounds().pollLast();
            NavigableSet<Match> correctedInput = Sets.newTreeSet();
            Map<Pairing, Match> matchesReceived =
                    thisRoundResults.stream().collect(Collectors.toMap(m -> m.getPairing(), Function.identity()));
            for (Match m : expectedRound.getMatches()) {
                Pairing p = m.getPairing();
                if (!matchesReceived.containsKey(p)) {
                    throw new IllegalArgumentException("Pairing " + p + " does not have a result!");
                }
                Match result = matchesReceived.get(p);
                if (p.isBye()) {
                    // note that the bye is always player 1
                    correctedInput.add(new Match(p, new Result(0, 2, 0), false, result.isP2Drop()));
                } else {
                    validateResult(result);
                    correctedInput.add(result);
                }
                matchesReceived.remove(p);
            }
            if (matchesReceived.size() > 0) {
                throw new IllegalArgumentException("Match results " + matchesReceived + " were unexpected!");
            }
            this.data.getRounds().add(new Round(round, true, correctedInput));
            if (isComplete()) {
                try {
                    this.data.setEndTime(ZonedDateTime.now());
                    this.db.writeTournament(this.data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
            currentRound += 1;
            return new Round(
                    currentRound,
                    false,
                    getPairingsAndAddToResults(
                            TournamentState.createTournamentState(
                                    this.data.getInput().getPlayers(),
                                    this.data.getRounds())));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    // should be called from within a lock
    private NavigableSet<Match> getPairingsAndAddToResults(TournamentState state) {
        int nextRound = getCurrentRound() + 1;
        NavigableSet<Pairing> pairings = calculatePairings(state, nextRound);
        NavigableSet<Match> matches = Sets.newTreeSet(
                pairings.stream().map(pairing -> new Match(pairing, Result.INCOMPLETE, false, false)).collect(
                        Collectors.toSet()));
        Map<Player, Match> tmp = Maps.newHashMap();
        for (Match m : matches) {
            tmp.put(m.getPairing().getPlayer1(), m);
            tmp.put(m.getPairing().getPlayer2(), m);
        }
        this.data.getRounds().add(new Round(nextRound, false, matches));
        try {
            this.db.writeTournament(this.data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return matches;
    }

    // should be called from within a lock
    private NavigableSet<Pairing> calculatePairings(TournamentState state, int round) {
        Map<Player, TieBreakers> tieBreakers;
        // special casing here to handle rounds before the final round
        if (round == this.data.getNumberOfRounds()) { // in the last round, use true tiebreakers
            tieBreakers = TieBreakers.getTieBreakers(state.getPlayers(), this.data.getRounds(), this.data.getId());
        } else {
            Map<Player, Integer> pointsPerPlayer = TieBreakers.calculatePointsPerPlayer(this.data.getRounds());
            tieBreakers = zeroTieBreakers(round, state.getPlayers(), pointsPerPlayer);
        }
        return innerCalculatePairings(state, tieBreakers);
    }
    
    // should be called from within a lock
    private Map<Player, TieBreakers> zeroTieBreakers(int round, Set<Player> players, Map<Player, Integer> pointsPerPlayer) {
            return players.stream().collect(Collectors.toMap(
                    Function.identity(),
                    player -> new TieBreakers(
                            player,
                            pointsPerPlayer.getOrDefault(player, 0),
                            0,
                            0,
                            0,
                            TieBreakers.generateRandomTieBreaker(this.data.getId(), player.getId(), round))));
    }

    protected abstract NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                                    Map<Player, TieBreakers> tieBreakers);

    @Override
    public NavigableSet<TieBreakers> getTieBreakers(Optional<Integer> roundRequested) {
        this.lock.readLock().lock();
        try {
            int round = roundToUse(roundRequested);
            if (round == 0) {
            	//special case here to produce rankings that can be used for seating
            	return Sets.newTreeSet(zeroTieBreakers(1, Sets.newHashSet(this.data.getInput().getPlayers()), ImmutableMap.of()).values());
            }
            Collection<Round> truncatedResults =
                    this.data.getRounds().stream().filter(r -> r.getNumber() <= round).collect(Collectors.toSet());
            // when requested by the user, we should always return the tiebreakers as if this is the
            // final round
            return Sets.newTreeSet(
                    TieBreakers.getTieBreakers(this.data.getInput().getPlayers(), truncatedResults, this.data.getId()).values());
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
