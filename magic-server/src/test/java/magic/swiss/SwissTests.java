package magic.swiss;

import java.util.List;
import java.util.NavigableSet;

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
    public void TestAlreadyPaired() {
        List<Player> players = new ImmutableList.Builder<Player>().add(new Player("jeff")).add(new Player("kimberly")).add(new Player("sam")).add(new Player("red hulk")).build();
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
}
