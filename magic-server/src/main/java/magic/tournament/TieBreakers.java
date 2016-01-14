package magic.tournament;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;

import magic.data.Match;
import magic.data.Player;
import magic.data.Round;

public class TieBreakers implements Comparable<TieBreakers> {
	
	public static final TieBreakers BYE = new TieBreakers(Player.BYE, 0, 0, 0, 0, "");

    private static final double MAX_POINTS_PER_MATCH            = 3.0;
    private static final double MAX_POINTS_PER_GAME             = MAX_POINTS_PER_MATCH;
    // note that the terminology "match win" and "game win" is used here to be consistent. however
    // "match point" and "game point" are more correct
    private static final double MINIMUM_OPPONENT_WIN_PERCENTAGE = .33;

    // for the unlikely case that a player plays no games
    private static final double DEFAULT_MATCH_WIN_PERCENTAGE = 0;
    private static final double DEFAULT_GAME_WIN_PERCENTAGE  = DEFAULT_MATCH_WIN_PERCENTAGE;

    private final Player player;
    private final int    matchPoints;
    private final double opponentMatchWinPercentage;
    private final double gameWinPercentage;
    private final double opponentGameWinPercentage;
    private final String finalTiebreaker;

    // arbitrarily deciding to round to eight decimal places
    private static double round(double input) {
        return Math.round(input * 100000000d) / 100000000d;
    }

    /*
     * Tiebreakers have two different modes. In the final round, they include all stats, but before
     * they they only use total points and the random tiebreaker
     */
    public static Map<Player, TieBreakers> getTieBreakers(Collection<Player> players,
                                                          Collection<Round> results,
                                                          String tournamentId) {
        Map<Player, Integer> pointsPerPlayer = calculatePointsPerPlayer(results);
        Map<Player, Collection<Match>> flatResults = getFlatResults(results);
        Map<Player, Double> opponentMatchWinPercentages = calculateOpponentWinPercentages(
                flatResults,
                calculatePlayerMatchWinPercentages(flatResults, pointsPerPlayer),
                DEFAULT_MATCH_WIN_PERCENTAGE);
        Map<Player, Double> playerGameWinPercentages = calculatePlayerGameWinPercentages(flatResults);
        Map<Player, Double> opponentGameWinPercentages = calculateOpponentWinPercentages(
                flatResults,
                playerGameWinPercentages,
                DEFAULT_GAME_WIN_PERCENTAGE);

        return players.stream().collect(Collectors.toMap(
                Function.identity(),
                player -> new TieBreakers(
                        player,
                        pointsPerPlayer.getOrDefault(player, 0),
                        opponentMatchWinPercentages.getOrDefault(player, 0.0),
                        playerGameWinPercentages.getOrDefault(player, 0.0),
                        opponentGameWinPercentages.getOrDefault(player, 0.0),
                        generateRandomTieBreaker(tournamentId, player.getId(), results.size()))));
    }

    public static Map<Player, Integer> calculatePointsPerPlayer(Collection<Round> results) {
        Map<Player, Integer> pointsPerPlayer = Maps.newHashMap();
        results.stream().filter(r -> r.isComplete()).forEach(r -> {
            r.getMatches().forEach(m -> {
                Player player1 = m.getPairing().getPlayer1();
                Player player2 = m.getPairing().getPlayer2();
                pointsPerPlayer.merge(player1, m.getPointsForPlayer(player1), Integer::sum);
                pointsPerPlayer.merge(player2, m.getPointsForPlayer(player2), Integer::sum);
            });
        });
        return pointsPerPlayer;
    }

    /**
     * Creates a value that is intended to be compared against similarly generated values to ensure
     * that each player is paired against a (pseudo)random but deterministic opponent in each round.
     * Each input is a parameter for which the output should be unique.
     *
     * @param tournamentId
     * @param playerId
     * @param round
     * @return
     */
    public static String generateRandomTieBreaker(String tournamentId, long playerId, Integer round) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String base = tournamentId + playerId + round;
            return BaseEncoding.base32Hex().encode(md.digest(base.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public TieBreakers(
                       @JsonProperty("player") Player player,
                       @JsonProperty("matchPoints") int matchPoints,
                       @JsonProperty("opponentMatchWinPercentage") double opponentMatchWinPercentage,
                       @JsonProperty("gameWinPercentage") double gameWinPercentage,
                       @JsonProperty("oppponentGameWinPercentage") double opponentGameWinPercentage,
                       @JsonProperty("finalTiebreaker") String finalTiebreaker) {
        this.player = player;
        this.matchPoints = matchPoints;
        this.opponentMatchWinPercentage = opponentMatchWinPercentage;
        this.gameWinPercentage = gameWinPercentage;
        this.opponentGameWinPercentage = opponentGameWinPercentage;
        this.finalTiebreaker = finalTiebreaker;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMatchPoints() {
        return matchPoints;
    }

    public double getOpponentMatchWinPercentage() {
        return opponentMatchWinPercentage;
    }

    public double getGameWinPercentage() {
        return gameWinPercentage;
    }

    public double getOpponentGameWinPercentage() {
        return opponentGameWinPercentage;
    }

    public String getFinalTiebreaker() {
        return finalTiebreaker;
    }

    private static final int GREATER = 1;
    private static final int LESS    = -1;

    @Override
    public int compareTo(TieBreakers other) {
    	if (player.equals(Player.BYE)) {
    		return LESS;
    	}
    	if (other.player.equals(Player.BYE)) {
    		return GREATER;
    	}
        if (matchPoints != other.matchPoints) {
            return matchPoints - other.matchPoints;
        }
        if (opponentMatchWinPercentage != other.opponentMatchWinPercentage) {
            return opponentMatchWinPercentage > other.opponentMatchWinPercentage ? GREATER : LESS;
        }
        if (gameWinPercentage != other.gameWinPercentage) {
            return gameWinPercentage > other.gameWinPercentage ? GREATER : LESS;
        }
        if (opponentGameWinPercentage != other.opponentGameWinPercentage) {
            return opponentGameWinPercentage > other.opponentGameWinPercentage ? GREATER : LESS;
        }
        if (!Objects.equals(finalTiebreaker, other.finalTiebreaker)) {
        	return finalTiebreaker.compareTo(other.finalTiebreaker);
        }
        return player.compareTo(other.player);
    }

    private static Map<Player, Collection<Match>> getFlatResults(Collection<Round> results) {
        return results.stream().collect(
                HashMultimap::<Player, Match> create,
                (map, round) -> {
                    round.getMatches().forEach(m -> {
                        map.put(m.getPairing().getPlayer1(), m);
                        map.put(m.getPairing().getPlayer2(), m);
                    });
                } ,
                (finalMap, map) -> finalMap.putAll(map)).asMap();
    }

    @VisibleForTesting
    /* package */ static Map<Player, Double> calculatePlayerMatchWinPercentages(Map<Player, Collection<Match>> flatResults,
                                                                                Map<Player, Integer> pointsPerPlayer) {
        return flatResults.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
            if (entry.getValue().size() == 0) {
                return 0.0;
            }
            return (double) pointsPerPlayer.getOrDefault(entry.getKey(), 0)
                    / (entry.getValue().size() * MAX_POINTS_PER_MATCH);
        }));
    }

    @VisibleForTesting
    /* package */ static Map<Player, Double> calculatePlayerGameWinPercentages(Map<Player, Collection<Match>> flatResults) {
        return flatResults.entrySet().stream().collect(Collectors.toMap(Entry::getKey, resultsPerPlayer -> {
            Player player = resultsPerPlayer.getKey();
            int games = resultsPerPlayer.getValue()
                    .stream()
                    .reduce(0, (sum, result) -> sum += result.getResult().getNumberOfGames(), Integer::sum);
            if (games == 0) {
                return 0.0;
            }
            int wins = resultsPerPlayer.getValue()
                    .stream()
                    .reduce(0, (sum, result) -> sum += result.getGamePointsForPlayer(player), Integer::sum);
            return round(wins / (games * MAX_POINTS_PER_GAME));
        }));
    }

    // TODO(brian): is this behavior correct?
    private static Predicate<Match> distinctByOpponent(Player player) {
        ConcurrentSkipListSet<Player> seen = new ConcurrentSkipListSet<>();
        return r -> seen.add(r.getPairing().getOpponent(player));
    }

    @VisibleForTesting
    /* package */ static Map<Player, Double> calculateOpponentWinPercentages(Map<Player, Collection<Match>> flatResults,
                                                                             Map<Player, Double> playerWinPercentage,
                                                                             Double defaultResult) {
        return flatResults.entrySet().stream().collect(Collectors.toMap(Entry::getKey, resultsPerPlayer -> {
            if (resultsPerPlayer.getValue().size() == 0) {
                return defaultResult;
            }
            Player player = resultsPerPlayer.getKey();
            return round(resultsPerPlayer.getValue()
                    .stream()
                    .filter(distinctByOpponent(player))
                    .filter(r -> !r.getPairing().isBye())
                    .collect(
                            Collectors.averagingDouble(
                                    result -> Math.max(
                                            MINIMUM_OPPONENT_WIN_PERCENTAGE,
                                            playerWinPercentage.get(result.getPairing().getOpponent(player))))));
        }));
    }

    @Override
    public String toString() {
        return "TieBreakers [player=" + player + ", matchPoints=" + matchPoints + ", opponentMatchWinPercentage="
                + opponentMatchWinPercentage + ", gameWinPercentage=" + gameWinPercentage
                + ", opponentGameWinPercentage=" + opponentGameWinPercentage + ", finalTiebreaker="
                + finalTiebreaker + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(finalTiebreaker);
        long temp;
        temp = Double.doubleToLongBits(gameWinPercentage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + matchPoints;
        temp = Double.doubleToLongBits(opponentGameWinPercentage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(opponentMatchWinPercentage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((player == null) ? 0 : player.hashCode());
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
        TieBreakers other = (TieBreakers) obj;
        if (!Objects.equals(finalTiebreaker, other.finalTiebreaker)) {
            return false;
        }
        if (Double.doubleToLongBits(gameWinPercentage) != Double.doubleToLongBits(other.gameWinPercentage)) {
            return false;
        }
        if (matchPoints != other.matchPoints) {
            return false;
        }
        if (Double.doubleToLongBits(opponentGameWinPercentage) != Double
                .doubleToLongBits(other.opponentGameWinPercentage)) {
            return false;
        }
        if (Double.doubleToLongBits(opponentMatchWinPercentage) != Double
                .doubleToLongBits(other.opponentMatchWinPercentage)) {
            return false;
        }
        if (player == null) {
            if (other.player != null) {
                return false;
            }
        } else if (!player.equals(other.player)) {
            return false;
        }
        return true;
    }

}
