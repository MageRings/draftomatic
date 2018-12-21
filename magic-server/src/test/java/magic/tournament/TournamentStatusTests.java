package
        magic.tournament;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
import magic.data.tournament.TournamentStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public final class TournamentStatusTests {

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
                        "Test basic status",
                        ImmutableList.of(MIKE, KIMBERLY),
                        Sets.newTreeSet(Lists.newArrayList(
                                new Round(1, false, Sets.newTreeSet(ImmutableSet.of(
                                        new Match(p1, Result.INCOMPLETE, false, false))))))
                },
        });
    }

    private final List<Player>         players;
    private final NavigableSet<Round> rounds;

    public TournamentStatusTests(String testName,
                                 List<Player> players,
                                 NavigableSet<Round> rounds
    ) {
        this.players = players;
        this.rounds = rounds;
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
        final TournamentStatus status = t.getStatus();
        Assert.assertEquals(1, status.getCurrentRound());
        Assert.assertEquals(false, status.isComplete());
        Assert.assertEquals(ImmutableList.of(MIKE, KIMBERLY), status.getSeatings());
    }
}
