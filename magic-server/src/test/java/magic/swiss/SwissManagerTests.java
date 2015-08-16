package magic.swiss;

import org.junit.Assert;
import org.junit.Test;

public class SwissManagerTests {

    @Test
    public void testDefaultNumberOfRounds() {
        Assert.assertEquals(1, SwissManager.getDefaultNumberOfRounds(2));
        Assert.assertEquals(2, SwissManager.getDefaultNumberOfRounds(3));
        Assert.assertEquals(2, SwissManager.getDefaultNumberOfRounds(4));
        Assert.assertEquals(3, SwissManager.getDefaultNumberOfRounds(5));
        Assert.assertEquals(3, SwissManager.getDefaultNumberOfRounds(8));
        Assert.assertEquals(4, SwissManager.getDefaultNumberOfRounds(9));
        Assert.assertEquals(4, SwissManager.getDefaultNumberOfRounds(16));
        Assert.assertEquals(5, SwissManager.getDefaultNumberOfRounds(20));
    }
}
