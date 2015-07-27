package magic.swiss;

import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import jersey.repackaged.com.google.common.collect.Lists;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;

public class SwissTests {

    @Test
    public void testAlreadyPaired() {
        List<Player> players =
                new ImmutableList.Builder<Player>()
                .add(new Player("jeff", 1))
                .add(new Player("kimberly", 2))
                .add(new Player("sam", 3))
                .add(new Player("red hulk", 4))
                .build();
        SwissTournament manager = new SwissTournament(players);
        NavigableSet<Pairing> pairings = manager.getPairings(1);
        Assert.assertEquals(pairings.size(), 2);
        List<Result> results = Lists.newArrayList();
        for (Pairing p : pairings) {
            results.add(new Result(p, 2, 0, 0));
        }
        manager.registerResults(1, results);
        NavigableSet<Pairing> expected = Sets.newTreeSet();
        expected.add(new Pairing(pairings.first().getPlayer1(), pairings.last().getPlayer1(), 6));
        expected.add(new Pairing(pairings.first().getPlayer2(), pairings.last().getPlayer2(), 0));
        Assert.assertEquals(expected, manager.getPairings(2));
    }

    @Test
    public void testByes() {
        List<Player> players = new ImmutableList.Builder<Player>()
                .add(new Player("kimberly", 2))
                .add(new Player("sam", 3))
                .add(new Player("red hulk", 4))
                .build();
        SwissTournament manager = new SwissTournament(players);
        NavigableSet<Pairing> pairings = manager.getPairings(1);
        Assert.assertEquals(pairings.size(), 2);
        Set<Result> results = pairings.stream()
                .map(p -> new Result(p, 2, 0, 0)) // should correct even if we mark the bye as
                // winning
                .collect(Collectors.toSet());
        results = manager.registerResults(1, results);
        List<Player> winners = results.stream()
                .map(r -> r.determineOutcome() == Result.Outcome.P1_WIN ?
                    r.getPairing().getPlayer1() : r.getPairing().getPlayer2())
                .collect(Collectors.toList());
        Player loser = results.stream()
                .filter(r -> !r.getPairing().getPlayer1().equals(Player.BYE))
                .findFirst().get().getPairing().getPlayer2();

        NavigableSet<Pairing> expected = Sets.newTreeSet();
        expected.add(new Pairing(winners.get(0), winners.get(1), 6));
        expected.add(new Pairing(Player.BYE, loser, 0));
        Assert.assertEquals(expected, manager.getPairings(2));
    }
}
