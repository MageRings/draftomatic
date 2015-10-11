package magic.tournament.swiss;

import org.junit.Assert;
import org.junit.Test;

import magic.tournament.swiss.SwissTournament;

public class SwissTournamentTests {

    @Test
    public void testDefaultNumberOfRounds() {
        Assert.assertEquals(1, SwissTournament.getDefaultNumberOfRounds(2));
        Assert.assertEquals(2, SwissTournament.getDefaultNumberOfRounds(3));
        Assert.assertEquals(2, SwissTournament.getDefaultNumberOfRounds(4));
        Assert.assertEquals(3, SwissTournament.getDefaultNumberOfRounds(5));
        Assert.assertEquals(3, SwissTournament.getDefaultNumberOfRounds(8));
        Assert.assertEquals(4, SwissTournament.getDefaultNumberOfRounds(9));
        Assert.assertEquals(4, SwissTournament.getDefaultNumberOfRounds(16));
        Assert.assertEquals(5, SwissTournament.getDefaultNumberOfRounds(20));
    }
}
