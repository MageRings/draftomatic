package magic.swiss;

import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.data.Round;

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
        Round round = Iterables.getOnlyElement(manager.getStatus().getRounds());
        Assert.assertEquals(round.getMatches().size(), 2);
        NavigableSet<Match> matches = Sets.newTreeSet(round.getMatches().stream().map(match ->
                new Match(match.getPairing(), new Result(2, 0, 0))).collect(Collectors.toSet()));
        Round actual = manager.registerResults(Optional.of(round.getNumber()), matches);

        NavigableSet<Match> expectedMatches = Sets.newTreeSet();
        expectedMatches.add(new Match(new Pairing(matches.first().getPairing().getPlayer1(), matches.last().getPairing().getPlayer1(), 6), Result.INCOMPLETE));
        expectedMatches.add(new Match(new Pairing(matches.first().getPairing().getPlayer2(), matches.last().getPairing().getPlayer2(), 0), Result.INCOMPLETE));
        Round expected = new Round(round.getNumber() + 1, expectedMatches, false);
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void testByes() {
        List<Player> players = new ImmutableList.Builder<Player>()
                .add(new Player(2, "kimberly"))
                .add(new Player(3, "sam"))
                .add(new Player(4, "red hulk"))
                .build();
        SwissTournament manager = new SwissTournament("id", 100, players);
        Round round = Iterables.getOnlyElement(manager.getStatus().getRounds());
        Assert.assertEquals(round.getMatches().size(), 2);
        Set<Match> matches = round.getMatches().stream()
                .map(match -> new Match(match.getPairing(), new Result(2, 0, 0))) // should correct even if we mark the bye as winning
                .collect(Collectors.toSet());
        Round actual = manager.registerResults(Optional.of(round.getNumber()), matches);
        Player loser = matches.stream()
                .filter(m -> !m.getPairing().getPlayer1().equals(Player.BYE))
                .findFirst().get().getPairing().getPlayer2();
        List<Player> winners = players.stream().filter(p -> !p.equals(loser)).collect(Collectors.toList());

        NavigableSet<Match> expectedMatches = Sets.newTreeSet();
        expectedMatches.add(new Match(new Pairing(winners.get(0), winners.get(1), 6), Result.INCOMPLETE));
        expectedMatches.add(new Match(new Pairing(Player.BYE, loser, 0), Result.INCOMPLETE));
        Assert.assertEquals(new Round(round.getNumber() + 1, expectedMatches, false).toString(), actual.toString());
    }
}
