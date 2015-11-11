package magic.data.database;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import magic.data.Player;

public class DbUtils {
	
	private DbUtils() {
		throw new UnsupportedOperationException("Not intended for instantiation.");
	}

    public static Set<Player> registerPlayers(List<String> newPlayers, Set<Player> currentPlayers) {
    	Set<Player> addedPlayers = new HashSet<>();
    	int lengthBefore = currentPlayers.size();
    	for (int i = 0; i < newPlayers.size(); i++) {
    		Player p = new Player(lengthBefore + i + 1, newPlayers.get(i));
    		addedPlayers.add(p);
    	}
    	return addedPlayers;
    }
}
