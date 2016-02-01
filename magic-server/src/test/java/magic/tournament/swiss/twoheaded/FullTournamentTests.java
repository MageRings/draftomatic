package magic.tournament.swiss.twoheaded;

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
import magic.tournament.swiss.GraphPairing;

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
    private static final Player BRIAN = new Player(5, "Brian");
    private static final Player JIM= new Player(6, "Jim");
    private static final Player GREEN_HORNET= new Player(7, "Green hornet");
    private static final Player ARM_FALL_OFF_BOY= new Player(8, "Arm fall off boy");

    private static final Pairing p1 = new Pairing(MIKE, BRIAN, 0);
    private static final Pairing p2 = new Pairing(KIMBERLY, JIM, 0);
    private static final Pairing p3 = new Pairing(SAM, GREEN_HORNET, 0);
    private static final Pairing p4 = new Pairing(RED_HULK, ARM_FALL_OFF_BOY, 0);

    private static final Pairing p5 = new Pairing(BRIAN, SAM, 6);
    private static final Pairing p6 = new Pairing(JIM, RED_HULK, 6);
    private static final Pairing p7 = new Pairing(MIKE, GREEN_HORNET, 0);
    private static final Pairing p8 = new Pairing(KIMBERLY, ARM_FALL_OFF_BOY, 0);

    private static final Result p1Win    = new Result(1, 0, 0);
    private static final Result p2Win    = new Result(0, 1, 0);

    private static final Pairing x1 = new Pairing(MIKE, Player.BYE, 0);
    private static final Pairing x2 = new Pairing(KIMBERLY, Player.BYE, 0);
    private static final Pairing x3 = new Pairing(SAM, BRIAN, 0);
    private static final Pairing x4 = new Pairing(RED_HULK, JIM, 0);

    private static final Pairing x5 = new Pairing(BRIAN, Player.BYE, 0);
    private static final Pairing x6 = new Pairing(JIM, Player.BYE, 0);
    private static final Pairing x7 = new Pairing(MIKE, SAM, 6);
    private static final Pairing x8 = new Pairing(KIMBERLY, RED_HULK, 6);

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "Basic smoke test",
                        ImmutableList.of(MIKE, KIMBERLY, SAM, RED_HULK, BRIAN, JIM, GREEN_HORNET, ARM_FALL_OFF_BOY),
                        ImmutableList.of(
                                new Round(1, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, Result.BYE, false, false),
                                        new Match(p2, Result.BYE, false, false),
                                        new Match(p3, p1Win, false, false),
                                        new Match(p4, p1Win, false, false)))),
                                new Round(2, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p5, Result.BYE, false, false),
                                        new Match(p6, Result.BYE, false, false),
                                        new Match(p7, p1Win, false, false),
                                        new Match(p8, p1Win, false, false))))),
                        ImmutableSortedSet.of(
                                new TieBreakers(GREEN_HORNET, 0, .5, 0, .5, IGNORED),
                                new TieBreakers(ARM_FALL_OFF_BOY, 0, .5, 0, .5, IGNORED),
                                new TieBreakers(MIKE, 3, .666667, .5, .666667, IGNORED),
                                new TieBreakers(KIMBERLY, 3, .666667, .5, .666667, IGNORED),
                                new TieBreakers(SAM, 3, .666667, .5, .666667, IGNORED),
                                new TieBreakers(RED_HULK, 3, .666667, .5, .666667, IGNORED),
                                new TieBreakers(BRIAN, 6, .5, 1, .5, IGNORED),
                                new TieBreakers(JIM, 6, .5, 1, .5, IGNORED)),
                },
                {
                        "With a byte",
                        ImmutableList.of(MIKE, KIMBERLY, SAM, RED_HULK, BRIAN, JIM),
                        ImmutableList.of(
                                new Round(1, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(x1, p2Win, false, false),
                                        new Match(x2, p2Win, false, false),
                                        new Match(x3, p1Win, false, false),
                                        new Match(x4, p1Win, false, false)))),
                                new Round(2, true, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(x5, p2Win, false, false),
                                        new Match(x6, p2Win, false, false),
                                        new Match(x7, p1Win, false, false),
                                        new Match(x8, p1Win, false, false))))),
                        ImmutableSortedSet.of(
                                new TieBreakers(BRIAN, 3, .5, .666667, .5, IGNORED),
                                new TieBreakers(JIM, 3, .5, .666667, .5, IGNORED),
                                new TieBreakers(SAM, 3, .75, .5, .833333, IGNORED),
                                new TieBreakers(RED_HULK, 3, .75, .5, .833333, IGNORED),
                                new TieBreakers(MIKE, 6, .5, 1, .5, IGNORED),
                                new TieBreakers(KIMBERLY, 6, .5, 1, .5, IGNORED)),
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
        Tournament tournament = new TwoHeadedSwissTournament(
                NoopDB.NOOPDB,
                TOURNAMENT_ID,
                new TournamentInput(Format.LIMITED_2HG_DRAFT, "ignore", this.players),
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
                        .sorted()
                        .collect(Collectors.toList())
                        .toString());
    }
}
