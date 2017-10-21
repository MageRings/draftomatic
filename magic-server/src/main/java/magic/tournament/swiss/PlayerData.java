package magic.tournament.swiss;

import java.util.Set;

import magic.data.Player;

public final class PlayerData {

    private final Player      player;
    private final int         points;
    private final Set<Player> alreadyMatched;

    public PlayerData(Player player, int points, Set<Player> alreadyMatched) {
        this.player = player;
        this.points = points;
        this.alreadyMatched = alreadyMatched;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPoints() {
        return points;
    }

    public Set<Player> getAlreadyMatched() {
        return alreadyMatched;
    }

    @Override
    public String toString() {
        return "PlayerData [player=" + player + ", points=" + points + ", alreadyMatched=" + alreadyMatched + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alreadyMatched == null) ? 0 : alreadyMatched.hashCode());
        result = prime * result + ((player == null) ? 0 : player.hashCode());
        result = prime * result + points;
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
        PlayerData other = (PlayerData) obj;
        if (alreadyMatched == null) {
            if (other.alreadyMatched != null) {
                return false;
            }
        } else if (!alreadyMatched.equals(other.alreadyMatched)) {
            return false;
        }
        if (player == null) {
            if (other.player != null) {
                return false;
            }
        } else if (!player.equals(other.player)) {
            return false;
        }
        return points == other.points;
    }
}
