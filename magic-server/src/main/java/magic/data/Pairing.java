package magic.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Pairing implements Comparable<Pairing> {

    private final Player player1;
    private final Player player2;
    private final int    totalPoints;

    public Pairing(@JsonProperty("player1") Player player1,
                   @JsonProperty("player2") Player player2,
                   @JsonProperty("totalPoints") int totalPoints) {
        this.totalPoints = Preconditions.checkNotNull(totalPoints);
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
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public int getTotalPoints() {
        return this.totalPoints;
    }

    @JsonIgnore
    public boolean isBye() {
        return this.player1.equals(Player.BYE);
    }

    @JsonIgnore
    public boolean isPlayer1(Player player) {
        if (player.equals(this.player1)) {
            return true;
        }
        if (player.equals(this.player2)) {
            return false;
        }
        throw new IllegalArgumentException("Player " + player + " was not involved in this pairing!");
    }

    @JsonIgnore
    public Player getOpponent(Player player) {
        if (isPlayer1(player)) {
            return this.player2;
        }
        return this.player1;
    }

    @Override
    public String toString() {
        return "Pairing [player1=" + this.player1 + ", player2=" + this.player2 + ", totalPoints=" + this.totalPoints
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.player1 == null) ? 0 : this.player1.hashCode());
        result = prime * result + ((this.player2 == null) ? 0 : this.player2.hashCode());
        result = prime * result + this.totalPoints;
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
        if (this.player1 == null) {
            if (other.player1 != null) {
                return false;
            }
        } else if (!this.player1.equals(other.player1)) {
            return false;
        }
        if (this.player2 == null) {
            if (other.player2 != null) {
                return false;
            }
        } else if (!this.player2.equals(other.player2)) {
            return false;
        }
        if (this.totalPoints != other.totalPoints) {
            return false;
        }
        return true;
    }

    // highest pairing sorts first
    @Override
    public int compareTo(Pairing o) {
        if (o == null) {
            return -1;
        }
        if (this.totalPoints != o.totalPoints) {
            return Integer.compare(o.totalPoints, this.totalPoints);
        }
        if (this.player1.compareTo(o.player1) != 0) {
            return o.player1.compareTo(this.player1);
        }
        return o.player2.compareTo(this.player2);
    }
}
