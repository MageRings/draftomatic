package magic.tournament;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import magic.data.Match;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.Result;
import magic.data.Round;
import magic.tournament.TieBreakers;

@RunWith(Parameterized.class)
public final class TieBreakersTests {

    private static final String IGNORED = "final tiebreaker ignored";

    private static final Player MIKE     = new Player(1, "Mike");
    private static final Player KIMBERLY = new Player(2, "Kimberly");
    private static final Player SAM      = new Player(3, "Sam");
    private static final Player RED_HULK = new Player(4, "Red hulk");

    // total points are not relevant to tiebreaker calculations, only for sorting pairings
    private static final Pairing p1 = new Pairing(MIKE, KIMBERLY, 0);
    private static final Pairing p2 = new Pairing(SAM, RED_HULK, 0);
    private static final Pairing p3 = new Pairing(MIKE, RED_HULK, 0);
    private static final Pairing p4 = new Pairing(KIMBERLY, SAM, 0);

    private static final Result p1Win = new Result(2, 0, 0);
    private static final Result p2Win = new Result(1, 2, 0);

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
                                new TieBreakers(SAM, 3, .416667, .5, .366667, IGNORED),
                                new TieBreakers(RED_HULK, 3, .75, .4, .75, IGNORED),
                                new TieBreakers(MIKE, 6, .416667, 1, .366667, IGNORED)),
                },
        });
    }

    private final Collection<Player>        players;
    private final Collection<Round>         rounds;
    private final NavigableSet<TieBreakers> tieBreakers;

    public TieBreakersTests(String testName,
                            Collection<Player> players,
                            Collection<Round> rounds,
                            NavigableSet<TieBreakers> tieBreakers) {
        this.players = players;
        this.rounds = rounds;
        this.tieBreakers = tieBreakers;
    }

    @Test
    public void test() {
        Map<Player, TieBreakers> actual = TieBreakers.getTieBreakers(players, rounds, "id");
        Assert.assertEquals(
                tieBreakers.toString(),
                actual.values()
                        .stream()
                        .sorted()
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
    
    @Test
    public void roundingTest() {
    	Map<Player, Collection<Match>> flatResults = ImmutableMap.<Player, Collection<Match>>of(
    			KIMBERLY, ImmutableList.of(new Match(new Pairing(KIMBERLY, RED_HULK, 0), new Result(1, 1666666, 0), false, false)));
    	Map<Player, Double> actual = TieBreakers.calculatePlayerGameWinPercentages(flatResults);
    	Assert.assertTrue(.000001 > actual.get(KIMBERLY));
    	Assert.assertEquals(.000001, TieBreakers.roundOutput(actual.get(KIMBERLY)), .00000000000000001);
    }
    
    @Test
    public void finalTiebreakerTest() {
    	Assert.assertEquals("5N0E8EPDN76B3MDIMI7PUGF6UQEB5BH06FPD31CVVT6TNUHEBM60====",
    			TieBreakers.generateRandomTieBreaker("tid", 5, 4));
    	Assert.assertEquals("3TQ7PP0QA7VFA9BVQ6MENE2GL1G9LMQVO4KSR4264KUNSE3V7ACG====",
    			TieBreakers.generateRandomTieBreaker("id", 5, 4));
    	Assert.assertEquals("4H4SM4TASL5M623RV394AN4GUQV26V1QAI1ANAL7G011D5QIIOD0====",
    			TieBreakers.generateRandomTieBreaker("tid", 5, 3));
    	Assert.assertEquals("FRPQPFIU3A6K2OLEIRIG1FJRQMKT6S4ITCBQI0NL42J8F86J35QG====",
    			TieBreakers.generateRandomTieBreaker("tid", 6, 4));
    }
}
