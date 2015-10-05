package magic.tournament.swiss;

import java.util.Set;

import magic.data.Player;

public final class PlayerData {

    private final Player player;
    private final int points;
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
}
