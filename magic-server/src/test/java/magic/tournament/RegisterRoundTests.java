package magic.tournament;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import magic.data.Format;
import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.data.Round;
import magic.data.tournament.TournamentData;
import magic.data.tournament.TournamentInput;

@RunWith(Parameterized.class)
public final class RegisterRoundTests {

    private static final String TOURNAMENT_ID    = "id";
    private static final int    NUMBER_OF_ROUNDS = 3;

    private static final Player MIKE     = new Player(1, "Mike");
    private static final Player KIMBERLY = new Player(2, "Kimberly");
    private static final Player SAM      = new Player(3, "Sam");
    private static final Player CRAIG    = new Player(4, "Craig");
    private static final Player JACK     = new Player(5, "Jack");

    // total points are not relevant to tiebreaker calculations, only for sorting pairings
    private static final Pairing p1 = new Pairing(MIKE, KIMBERLY, 0);
    private static final Pairing p2 = new Pairing(SAM, CRAIG, 0);

    // for repairing test
    private static final Pairing p1Alternate = new Pairing(MIKE, CRAIG, 0);
    private static final Pairing p2Alternate = new Pairing(SAM, KIMBERLY, 0);
    private static final Pairing p2Bogus     = new Pairing(SAM, JACK, 0);

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "Test tournament complete",
                        ImmutableSortedSet.of(
                                new Round(1, true, Sets.newTreeSet()),
                                new Round(2, true, Sets.newTreeSet()),
                                new Round(3, true, Sets.newTreeSet())),
                        Optional.of(3),
                        null,
                        "This tournament is already compelete!"
                },
                {
                        "Test round not expected",
                        ImmutableSortedSet.of(
                                new Round(1, true, Sets.newTreeSet()),
                                new Round(2, true, Sets.newTreeSet())),
                        Optional.of(1),
                        null,
                        "You may only enter results for the current round (2)!",
                },
                {
                        "Test != 2 wins",
                        Sets.newTreeSet(Lists.newArrayList(
                                new Round(1, false, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, Result.INCOMPLETE, false, false)))))),
                        Optional.empty(),
                        Sets.newTreeSet(ImmutableSet.of(
                                new Match(p1, new Result(3, 1, 0), false, false))),
                        "Match Match [pairing=Pairing [player1=Player [id=1, name=Mike], player2=Player" +
                                " [id=2, name=Kimberly], totalPoints=0], result=Result [p1Wins=3, p2Wins=1, draws=0]," +
                                " p1Drop=false, p2Drop=false] is invalid." +
                                "  Matches are played until one player has two wins."
                },
                {
                        "Test negative wins",
                        Sets.newTreeSet(Lists.newArrayList(
                                new Round(1, false, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, Result.INCOMPLETE, false, false)))))),
                        Optional.empty(),
                        Sets.newTreeSet(ImmutableSet.of(
                                new Match(p1, new Result(2, -1, 0), false, false))),
                        "Match Match [pairing=Pairing [player1=Player [id=1, name=Mike], player2=Player" +
                                " [id=2, name=Kimberly], totalPoints=0], result=Result [p1Wins=2, p2Wins=-1, " +
                                "draws=0], p1Drop=false, p2Drop=false] is invalid." +
                                "  Players can not have a negative number of wins."
                },
                {
                        "Test repairing",
                        Sets.newTreeSet(Lists.newArrayList(
                                new Round(1, false, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, Result.INCOMPLETE, false, false),
                                        new Match(p2, Result.INCOMPLETE, false, false)))))),
                        Optional.empty(),
                        Sets.newTreeSet(ImmutableSet.of(
                                new Match(p1Alternate, new Result(2, 0, 0), false, false),
                                new Match(p2Alternate, new Result(0, 1, 0), false, false))),
                        null,
                },
                {
                        "Test unexpected player in repairing",
                        Sets.newTreeSet(Lists.newArrayList(
                                new Round(1, false, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, Result.INCOMPLETE, false, false),
                                        new Match(p2, Result.INCOMPLETE, false, false)))))),
                        Optional.empty(),
                        Sets.newTreeSet(ImmutableSet.of(
                                new Match(p1Alternate, new Result(2, 0, 0), false, false),
                                new Match(p2Bogus, new Result(0, 1, 0), false, false))),
                        "Did not expect to see player Player [id=5, name=Jack] in the results!",
                },
        });
    }

    private final NavigableSet<Round> rounds;
    private final Optional<Integer>   roundRequested;
    private final Collection<Match>   roundResults;
    private final String              errorExpected;

    public RegisterRoundTests(String testName,
                              NavigableSet<Round> rounds,
                              Optional<Integer> roundRequested,
                              Collection<Match> roundResults,
                              String errorExpected) {
        this.rounds = rounds;
        this.roundRequested = roundRequested;
        this.roundResults = roundResults;
        this.errorExpected = errorExpected;
    }

    @Test
    public void test() {
        Tournament t = new DummyTournament(
                new TournamentData(
                        TOURNAMENT_ID,
                        NUMBER_OF_ROUNDS,
                        new TournamentInput(Format.CONSTRUCTED_CASUAL, "", ImmutableList.<Player> of()),
                        ZonedDateTime.now(),
                        null,
                        this.rounds));
        try {
            t.registerResults(this.roundRequested, this.roundResults);
        } catch (Exception e) {
            if (this.errorExpected != null) {
                Assert.assertEquals(this.errorExpected, e.getMessage());
                return;
            }
            throw e;
        }
    }
}
