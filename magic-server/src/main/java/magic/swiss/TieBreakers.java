package magic.swiss;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.BaseEncoding;

import magic.data.Match;
import magic.data.Player;

public class TieBreakers implements Comparable<TieBreakers> {

    private static final double MAX_POINTS_PER_GAME = 3.0;
    //note that the terminology "match win" and "game win" is used here to be consistent.  however "match point" and
    //"game point" are more correct
    private static final double MINIMUM_MATCH_WIN_PERCENTAGE = .33;
    private static final double MINIMUM_GAME_WIN_PERCENTAGE = MINIMUM_MATCH_WIN_PERCENTAGE;

    //for the unlikely case that a player plays no games
    private static final double DEFAULT_MATCH_WIN_PERCENTAGE = 0;
    private static final double DEFAULT_GAME_WIN_PERCENTAGE = DEFAULT_MATCH_WIN_PERCENTAGE;

    private final Player player;
    private final int matchPoints;
    private final double opponentMatchWinPercentage;
    private final double gameWinPercentage;
    private final double opponentGameWinPercentage;
    private final byte[] finalTiebreaker;

    public static Map<Player, TieBreakers> getTieBreakers(Collection<Map<Player, Match>> results, Map<Player, Integer> pointsPerPlayer, String tournamentId) {
        Map<Player, Collection<Match>> flatResults = getFlatResults(results);
        Map<Player, Double> opponentMatchWinPercentages = calculateOpponentWinPercentages(flatResults,
                calculatePlayerMatchWinPercentages(flatResults, pointsPerPlayer),
                DEFAULT_MATCH_WIN_PERCENTAGE);
        Map<Player, Double> playerGameWinPercentages = calculatePlayerGameWinPercentages(flatResults);
        Map<Player, Double> opponentGameWinPercentages = calculateOpponentWinPercentages(flatResults,
                playerGameWinPercentages,
                DEFAULT_GAME_WIN_PERCENTAGE);

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return pointsPerPlayer.keySet().stream().collect(Collectors.toMap(
                Function.identity(),
                player -> new TieBreakers(player,
                        pointsPerPlayer.get(player),
                        opponentMatchWinPercentages.get(player),
                        playerGameWinPercentages.get(player),
                        opponentGameWinPercentages.get(player),
                        md.digest((tournamentId + player.getId()).getBytes(StandardCharsets.UTF_8))
                )));
    }

    public Player getPlayer() {
        return player;
    }

    private static final int GREATER = 1;
    private static final int LESS = -1;
    @Override
    public int compareTo(TieBreakers other) {
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
        return BaseEncoding.base32Hex().encode(finalTiebreaker).compareTo(BaseEncoding.base32Hex().encode(other.finalTiebreaker));
    }

    @VisibleForTesting
    /* package */ TieBreakers(Player player, int matchPoints, double opponentMatchWinPercentage, double gameWinPercentage,
            double opponentGameWinPercentage, byte[] finalTiebreaker) {
        this.player = player;
        this.matchPoints = matchPoints;
        this.opponentMatchWinPercentage = opponentMatchWinPercentage;
        this.gameWinPercentage = gameWinPercentage;
        this.opponentGameWinPercentage = opponentGameWinPercentage;
        this.finalTiebreaker = finalTiebreaker;
    }

    private static Map<Player, Collection<Match>> getFlatResults(Collection<Map<Player, Match>> results) {
        return results.stream().collect(
                HashMultimap::<Player, Match>create,
                (map, resultsPerPlayer) -> map.putAll(Multimaps.forMap(resultsPerPlayer)),
                (finalMap, map) -> finalMap.putAll(map)).asMap();
    }

    @VisibleForTesting
    /* package */ static Map<Player, Double> calculatePlayerMatchWinPercentages(Map<Player, Collection<Match>> flatResults,
            Map<Player, Integer> pointsPerPlayer) {
        return flatResults.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
            if (entry.getValue().size() == 0) {
                return MINIMUM_MATCH_WIN_PERCENTAGE;
            }
            return Math.max(MINIMUM_MATCH_WIN_PERCENTAGE, (double) pointsPerPlayer.get(entry.getKey())/entry.getValue().size());
        }));
    }

    @VisibleForTesting
    /* package */ static Map<Player, Double> calculatePlayerGameWinPercentages(Map<Player, Collection<Match>> flatResults) {
        return flatResults.entrySet().stream().collect(Collectors.toMap(Entry::getKey, resultsPerPlayer -> {
            Player player = resultsPerPlayer.getKey();
            int games = resultsPerPlayer.getValue().stream().reduce(0, (sum, result) -> sum += result.getResult().getNumberOfGames(), Integer::sum);
            if (games == 0) {
                return MINIMUM_GAME_WIN_PERCENTAGE;
            }
            int wins = resultsPerPlayer.getValue().stream().reduce(0, (sum, result) -> sum += result.getGamePointsForPlayer(player), Integer::sum);
            return Math.max(MINIMUM_GAME_WIN_PERCENTAGE, wins / (games * MAX_POINTS_PER_GAME));
        }));
    }

    private static Predicate<Match> distinctByOpponent(Player player) {
        ConcurrentSkipListSet<Player> seen = new ConcurrentSkipListSet<>();
        return r -> seen.add(r.getPairing().getOpponent(player));
    }

    @VisibleForTesting
    /* package */ static  Map<Player, Double> calculateOpponentWinPercentages(Map<Player, Collection<Match>> flatResults,
            Map<Player, Double> playerWinPercentage, Double defaultResult) {
        return flatResults.entrySet().stream().collect(Collectors.toMap(Entry::getKey, resultsPerPlayer -> {
            if (resultsPerPlayer.getValue().size() == 0) {
                return defaultResult;
            }
            Player player = resultsPerPlayer.getKey();
            return resultsPerPlayer.getValue().stream()
                    .filter(distinctByOpponent(player))
                    .filter(r -> !r.getPairing().isBye())
                    .collect(Collectors.averagingDouble(result -> playerWinPercentage.get(result.getPairing().getOpponent(player))));
        }));
    }
}
