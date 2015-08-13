package magic.swiss;

import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;

public class SwissTests {

    @Test
    public void testAlreadyPaired() {
        List<Player> players =
                new ImmutableList.Builder<Player>()
                .add(new Player(1, "jeff"))
                .add(new Player(2, "kimberly"))
                .add(new Player(3, "sam"))
                .add(new Player(4, "red hulk"))
                .build();
        SwissTournament manager = new SwissTournament("id", 100, players);
        NavigableSet<Match> matches = manager.getStatus().getRounds().last().getMatches();
        Assert.assertEquals(matches.size(), 2);
        matches = Sets.newTreeSet(matches.stream().map(match -> new Match(match.getPairing(), new Result(2, 0, 0))).collect(Collectors.toSet()));
        manager.registerResults(Optional.of(1), matches);
        NavigableSet<Pairing> expected = Sets.newTreeSet();
        expected.add(new Pairing(pairings.first().getPlayer1(), pairings.last().getPlayer1(), 6));
        expected.add(new Pairing(pairings.first().getPlayer2(), pairings.last().getPlayer2(), 0));
        Assert.assertEquals(expected, manager.getPairings(Optional.of(2)));
    }

    @Test
    public void testByes() {
        List<Player> players = new ImmutableList.Builder<Player>()
                .add(new Player(2, "kimberly"))
                .add(new Player(3, "sam"))
                .add(new Player(4, "red hulk"))
                .build();
        SwissTournament manager = new SwissTournament("id", 100, players);
        NavigableSet<Pairing> pairings = manager.getPairings(Optional.of(1));
        Assert.assertEquals(pairings.size(), 2);
        Set<Result> results = pairings.stream()
                .map(p -> new Result(p, 2, 0, 0)) // should correct even if we mark the bye as
                // winning
                .collect(Collectors.toSet());
        results = manager.registerResults(Optional.of(1), results);
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
        Assert.assertEquals(expected, manager.getPairings(Optional.of(2)));
    }
}
