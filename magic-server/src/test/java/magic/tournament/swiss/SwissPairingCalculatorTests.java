package magic.tournament.swiss;

import java.util.Arrays;
import java.util.NavigableSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jersey.repackaged.com.google.common.collect.Sets;
import magic.data.Pairing;
import magic.data.Player;

@RunWith(Parameterized.class)
public class SwissPairingCalculatorTests {

    private static final Player MIKE     = new Player(1, "Mike");
    private static final Player KIMBERLY = new Player(2, "Kimberly");
    private static final Player SAM      = new Player(3, "Sam");
    private static final Player RED_HULK = new Player(4, "Red hulk");
    private static final Player BRIAN    = new Player(5, "Brian");
    private static final Player BILL     = new Player(6, "Bill");

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "Basic test",
                        new TournamentState(Lists.newArrayList(
                                new PlayerData(MIKE, 3, Sets.newHashSet()),
                                new PlayerData(KIMBERLY, 3, Sets.newHashSet()),
                                new PlayerData(SAM, 0, Sets.newHashSet()),
                                new PlayerData(RED_HULK, 0, Sets.newHashSet()))),
                        ImmutableMap.of(MIKE, 1, KIMBERLY, 2, SAM, 3, RED_HULK, 4),
                        ImmutableSet.of(new Pairing(MIKE, KIMBERLY, 6), new Pairing(SAM, RED_HULK, 0)),
                },
                {
                        "First round",
                        new TournamentState(Lists.newArrayList(
                                new PlayerData(MIKE, 0, Sets.newHashSet()),
                                new PlayerData(KIMBERLY, 0, Sets.newHashSet()),
                                new PlayerData(SAM, 0, Sets.newHashSet()),
                                new PlayerData(RED_HULK, 0, Sets.newHashSet()))),
                        ImmutableMap.of(MIKE, 1, KIMBERLY, 2, SAM, 3, RED_HULK, 4),
                        ImmutableSet.of(new Pairing(MIKE, KIMBERLY, 0), new Pairing(SAM, RED_HULK, 0)),
                },
                {
                        "Test already paired (double pair down)",
                        new TournamentState(Lists.newArrayList(
                                new PlayerData(MIKE, 3, Sets.newHashSet(KIMBERLY)),
                                new PlayerData(KIMBERLY, 3, Sets.newHashSet(MIKE)),
                                new PlayerData(SAM, 0, Sets.newHashSet(RED_HULK)),
                                new PlayerData(RED_HULK, 0, Sets.newHashSet(SAM)))),
                        ImmutableMap.of(MIKE, 1, KIMBERLY, 2, SAM, 3, RED_HULK, 4),
                        ImmutableSet.of(new Pairing(MIKE, SAM, 3), new Pairing(KIMBERLY, RED_HULK, 3)),
                },
                {
                        "Test pair down",
                        new TournamentState(Lists.newArrayList(
                                new PlayerData(MIKE, 3, Sets.newHashSet(SAM)),
                                new PlayerData(KIMBERLY, 3, Sets.newHashSet(SAM)),
                                new PlayerData(SAM, 3, Sets.newHashSet(MIKE, KIMBERLY)),
                                new PlayerData(RED_HULK, 0, Sets.newHashSet(BRIAN, BILL)),
                                new PlayerData(BRIAN, 0, Sets.newHashSet(RED_HULK)),
                                new PlayerData(BILL, 0, Sets.newHashSet(RED_HULK)))),
                        ImmutableMap.builder()
                                .put(MIKE, 1)
                                .put(KIMBERLY, 2)
                                .put(SAM, 3)
                                .put(RED_HULK, 4)
                                .put(BRIAN, 5)
                                .put(BILL, 6)
                                .build(),
                        ImmutableSet.of(
                                new Pairing(MIKE, KIMBERLY, 6),
                                new Pairing(SAM, RED_HULK, 3),
                                new Pairing(BRIAN, BILL, 0)),
                },
                {
                        "Test repairing",
                        new TournamentState(Lists.newArrayList(
                                new PlayerData(MIKE, 3, Sets.newHashSet(KIMBERLY, SAM, RED_HULK)),
                                new PlayerData(KIMBERLY, 3, Sets.newHashSet(MIKE, SAM, RED_HULK)),
                                new PlayerData(SAM, 0, Sets.newHashSet(RED_HULK, MIKE, KIMBERLY)),
                                new PlayerData(RED_HULK, 0, Sets.newHashSet(SAM, MIKE, KIMBERLY)))),
                        ImmutableMap.of(MIKE, 1, KIMBERLY, 2, SAM, 3, RED_HULK, 4),
                        ImmutableSet.of(new Pairing(MIKE, KIMBERLY, 6), new Pairing(SAM, RED_HULK, 0)),
                },
        });
    }

    private static SwissPairingCalculator calc = new GraphPairing();

    private TournamentState               state;
    private ImmutableMap<Player, Integer> rankings;
    private Set<Pairing>                  pairings;

    public SwissPairingCalculatorTests(String testName,
                                       TournamentState state,
                                       ImmutableMap<Player, Integer> rankings,
                                       Set<Pairing> pairings) {
        this.state = state;
        this.rankings = rankings;
        this.pairings = pairings;
    }

    @Test
    public void test() {
        NavigableSet<Pairing> result = calc.innerCalculatePairings(state, Maps.newLinkedHashMap(rankings));
        Assert.assertEquals(pairings, result);
    }
}
