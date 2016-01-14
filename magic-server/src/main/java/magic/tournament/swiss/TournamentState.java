package magic.tournament.swiss;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jersey.repackaged.com.google.common.collect.Lists;
import jersey.repackaged.com.google.common.collect.Maps;
import jersey.repackaged.com.google.common.collect.Sets;
import magic.data.Player;
import magic.data.Round;

public class TournamentState {

    private final Map<Player, PlayerData> roundData = Maps.newHashMap();

    public TournamentState(Collection<PlayerData> data) {
        for (PlayerData d : data) {
            roundData.put(d.getPlayer(), d);
        }
        if (roundData.size() % 2 != 0) {
        	Set<Player> alreadyHaveByes = data.stream()
        			.filter(d -> d.getAlreadyMatched().contains(Player.BYE))
        			.map(d -> d.getPlayer())
        			.collect(Collectors.toSet());
            roundData.put(Player.BYE, new PlayerData(Player.BYE, 0, alreadyHaveByes));
        }
    }

    public static TournamentState createTournamentState(List<Player> players, NavigableSet<Round> rounds) {
        Map<Player, Integer> points = Maps.newHashMap();
        Map<Player, Set<Player>> alreadyMatched = Maps.newHashMap();
        Set<Player> dropped = Sets.newHashSet();
        rounds.stream().filter(r -> r.isComplete()).forEach(r -> {
            r.getMatches().forEach(m -> {
                Player player1 = m.getPairing().getPlayer1();
                Player player2 = m.getPairing().getPlayer2();
                points.merge(player1, m.getPointsForPlayer(player1), Integer::sum);
                points.merge(player2, m.getPointsForPlayer(player2), Integer::sum);

                alreadyMatched.merge(player1, Sets.newHashSet(player2), Sets::union);
                alreadyMatched.merge(player2, Sets.newHashSet(player1), Sets::union);

                if (m.isP1Drop()) {
                    dropped.add(player1);
                }
                if (m.isP2Drop()) {
                    dropped.add(player2);
                }
            });
        });
        List<PlayerData> data = Lists.newArrayListWithCapacity(players.size());
        for (Player p : players) {
            if (dropped.contains(p)) {
                continue;
            }
            data.add(new PlayerData(p, points.getOrDefault(p, 0), alreadyMatched.getOrDefault(p, Sets.newHashSet())));
        }
        return new TournamentState(data);
    }

    public Map<Player, PlayerData> getRoundData() {
        return roundData;
    }

    public Map<Integer, List<Player>> getPlayersAtEachPointLevel() {
        return roundData.values()
                .stream()
                .collect(
                        Collectors.toMap(
                                d -> d.getPoints(),
                                d -> Lists.<Player> newArrayList(d.getPlayer()),
                                (l1, l2) -> {
                                    l1.addAll(l2);
                                    return l1;
                                }));
    }

    public int getNumberOfPlayers() {
        return roundData.size();
    }

    public Set<Player> getPlayers() {
        return roundData.keySet();
    }

    public <T> Map<Player, T> getSinglePurposeMap(Function<PlayerData, T> fetcher) {
        return roundData.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e -> fetcher.apply(e.getValue())));
    }

    @Override
    public String toString() {
        return "TournamentState [roundData=" + roundData + "]";
    }

}
