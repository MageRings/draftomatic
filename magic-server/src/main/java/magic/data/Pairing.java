package magic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Pairing implements Comparable<Pairing> {

    private final Player player1;
    private final Player player2;
    private final int totalPoints;

    public Pairing(@JsonProperty("player1") Player player1, @JsonProperty("player2") Player player2, @JsonProperty("totalPoints") int totalPoints) {
        this.totalPoints = totalPoints;
        Preconditions.checkNotNull(player1, "Player 1 cannot be null!");
        Preconditions.checkNotNull(player2, "Player 2 cannot be null!");
        if (player1.compareTo(player2) > 0) {
            this.player1 = player1;
            this.player2 = player2;
        } else {
            this.player1 = player2;
            this.player2 = player1;
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    @Override
    public String toString() {
        return "Pairing [player1=" + player1 + ", player2=" + player2 + ", totalPoints=" + totalPoints + "]";
    }

    @Override
    public int compareTo(Pairing o) {
        if (o == null) {
            return 1;
        }
        if (this.totalPoints != o.totalPoints) {
            return Integer.compare(totalPoints, o.totalPoints);
        }
        if (this.player1.compareTo(o.player1) != 0) {
            return player1.compareTo(o.player1);
        }
        return player2.compareTo(o.player2);
    }
}
