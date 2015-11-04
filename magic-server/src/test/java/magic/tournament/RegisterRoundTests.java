package magic.tournament;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

    // total points are not relevant to tiebreaker calculations, only for sorting pairings
    private static final Pairing p1 = new Pairing(MIKE, KIMBERLY, 0);

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "Test tournament complete",
                        ImmutableList.of(),
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
                        ImmutableList.of(),
                        ImmutableSortedSet.of(
                                new Round(1, true, Sets.newTreeSet()),
                                new Round(2, true, Sets.newTreeSet())),
                        Optional.of(1),
                        null,
                        "You may only enter results for the current round (2)!",
                },
                {
                        "Test illegal wins",
                        ImmutableList.of(MIKE, KIMBERLY),
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
        });
    }

    private final List<Player>         players;
    private final NavigableSet<Round> rounds;
    private final Optional<Integer>   roundRequested;
    private final Collection<Match>   roundResults;
    private final String              errorExpected;

    public RegisterRoundTests(String testName,
                              List<Player> players,
                              NavigableSet<Round> rounds,
                              Optional<Integer> roundRequested,
                              Collection<Match> roundResults,
                              String errorExpected) {
        this.players = players;
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
                        new TournamentInput(Format.CONSTRUCTED_CASUAL, "", this.players),
                        ZonedDateTime.now(),
                        null,
                        this.rounds));
        try {
            t.registerResults(this.roundRequested, this.roundResults);
        } catch (Exception e) {
            if (this.errorExpected != null) {
                System.out.println(e);
                Assert.assertEquals(this.errorExpected, e.getMessage());
                return;
            }
            throw e;
        }
    }
}
