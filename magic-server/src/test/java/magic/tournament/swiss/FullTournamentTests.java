package magic.tournament.swiss;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import magic.data.Format;
import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.data.Round;
import magic.data.database.NoopDB;
import magic.data.tournament.TournamentInput;
import magic.data.tournament.TournamentStatus;
import magic.tournament.TieBreakers;
import magic.tournament.Tournament;

/**
 * This class provides essentially full testing of the entire tournament engine. Ideally no special
 * cases are tested here as it is a fairly heavyweight framework for doing so.
 */
@RunWith(Parameterized.class)
public final class FullTournamentTests {

    private static final String IGNORED       = "final tiebreaker ignored";
    private static final String TOURNAMENT_ID = "id";

    private static final Player MIKE     = new Player(1, "Mike");
    private static final Player KIMBERLY = new Player(2, "Kimberly");
    private static final Player SAM      = new Player(3, "Sam");
    private static final Player RED_HULK = new Player(4, "Red hulk");

    private static final Pairing p1 = new Pairing(MIKE, KIMBERLY, 0);
    private static final Pairing p2 = new Pairing(SAM, RED_HULK, 0);
    private static final Pairing p3 = new Pairing(MIKE, RED_HULK, 6);
    private static final Pairing p4 = new Pairing(KIMBERLY, SAM, 0);
    private static final Pairing p5 = new Pairing(Player.BYE, SAM, 0);

    private static final Result p1Win    = new Result(2, 0, 0);
    private static final Result p2Win    = new Result(1, 2, 0);
    private static final Result p2WinAll = new Result(0, 2, 0);

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "Basic smoke test",
                        ImmutableList.of(MIKE, KIMBERLY, SAM, RED_HULK),
                        ImmutableList.of(
                                new Round(1, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, p1Win, false, false),
                                        new Match(p2, p2Win, false, false)))),
                                new Round(2, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p3, p1Win, false, false),
                                        new Match(p4, p2Win, false, false))))),
                        ImmutableSortedSet.of(
                                new TieBreakers(KIMBERLY, 0, .75, .2, .75, IGNORED),
                                new TieBreakers(SAM, 3, .415, .5, .365, IGNORED),
                                new TieBreakers(RED_HULK, 3, .75, .4, .75, IGNORED),
                                new TieBreakers(MIKE, 6, .415, 1, .365, IGNORED)),
                },
                {
                        "Drop a player",
                        ImmutableList.of(MIKE, KIMBERLY, SAM, RED_HULK),
                        ImmutableList.of(
                                new Round(1, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, p1Win, false, true),
                                        new Match(p2, p2Win, false, false)))),
                                new Round(2, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p3, p1Win, false, false),
                                        new Match(p5, p2WinAll, false, false))))),
                        ImmutableSortedSet.of(
                                new TieBreakers(KIMBERLY, 0, 1, 0, 1, IGNORED),
                                new TieBreakers(SAM, 3, .5, .6, .4, IGNORED),
                                new TieBreakers(RED_HULK, 3, .75, .4, .8, IGNORED),
                                new TieBreakers(MIKE, 6, .415, 1, .365, IGNORED)),
                },
        });
    }

    private final List<Player>               players;
    private final Collection<Round>         rounds;
    private final NavigableSet<TieBreakers> tieBreakers;

    public FullTournamentTests(String testName,
                               List<Player> players,
                               Collection<Round> rounds,
                               NavigableSet<TieBreakers> tieBreakers) {
        this.players = players;
        this.rounds = rounds;
        this.tieBreakers = tieBreakers;
    }

    @Test
    public void test() {
        Tournament tournament = new SwissTournament(
                NoopDB.NOOPDB,
                TOURNAMENT_ID,
                new TournamentInput(Format.LIMITED_DRAFT, "ignore", this.players),
                Optional.of(this.rounds.size()),
                new GraphPairing());
        tournament.initFirstRound();
        for (Round r : this.rounds) {
            TournamentStatus status = tournament.getStatus();
            Assert.assertEquals(r.getNumber(), status.getCurrentRound());
            Assert.assertEquals(this.rounds.size(), status.getTournamentData().getNumberOfRounds());
            Match[] actualMatches = status.getTournamentData().getRounds().last().getMatches().toArray(new Match[] {});
            Match[] expectedMatches = r.getMatches().toArray(new Match[] {});
            for (int i = 0; i < r.getMatches().size(); i++) {
                Assert.assertEquals(Result.INCOMPLETE, actualMatches[i].getResult());
                Assert.assertFalse(actualMatches[i].isP1Drop());
                Assert.assertFalse(actualMatches[i].isP2Drop());
                Assert.assertEquals(
                        expectedMatches[i].getPairing().toString(),
                        actualMatches[i].getPairing().toString());

            }
            // move on to the next round
            tournament.registerResults(Optional.empty(), ImmutableList.copyOf(expectedMatches));
        }
        NavigableSet<TieBreakers> actual = tournament.getTieBreakers(Optional.empty());
        Assert.assertEquals(
                this.tieBreakers.toString(),
                actual.stream()
                        .map(t -> new TieBreakers(
                                t.getPlayer(),
                                t.getMatchPoints(),
                                t.getOpponentMatchWinPercentage(),
                                t.getGameWinPercentage(),
                                t.getOpponentGameWinPercentage(),
                                IGNORED))
                        .collect(Collectors.toList())
                        .toString());
    }
}
