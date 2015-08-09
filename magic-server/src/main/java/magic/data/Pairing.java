package magic.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        if (player1.getId() < player2.getId()) {
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

    @JsonIgnore
    public boolean isPlayer1(Player player) {
        if (player.equals(player1)) {
            return true;
        }
        if (player.equals(player2)) {
            return false;
        }
        throw new IllegalArgumentException("Player " + player + " was not involved in this pairing!");
    }
    
    @JsonIgnore
    public Player getOpponent(Player player) {
        if (isPlayer1(player)) {
            return player2;
        }
        return player1;
    }

    @Override
    public String toString() {
        return "Pairing [player1=" + player1 + ", player2=" + player2 + ", totalPoints=" + totalPoints + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((player1 == null) ? 0 : player1.hashCode());
        result = prime * result + ((player2 == null) ? 0 : player2.hashCode());
        result = prime * result + totalPoints;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pairing other = (Pairing) obj;
        if (player1 == null) {
            if (other.player1 != null) {
                return false;
            }
        } else if (!player1.equals(other.player1)) {
            return false;
        }
        if (player2 == null) {
            if (other.player2 != null) {
                return false;
            }
        } else if (!player2.equals(other.player2)) {
            return false;
        }
        if (totalPoints != other.totalPoints) {
            return false;
        }
        return true;
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
