package magic.data.database;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Maps;
import magic.data.Player;

public class DbUtils {
	
	private DbUtils() {
		throw new UnsupportedOperationException("Not intended for instantiation.");
	}

    public static Map<String, Player> registerPlayers(List<String> newPlayers, Set<Player> currentPlayers) {
    	Map<String, Player> addedPlayers = Maps.newHashMap();
    	int lengthBefore = currentPlayers.size();
    	for (int i = 0; i < newPlayers.size(); i++) {
    		Player p = new Player(lengthBefore + i + 1, newPlayers.get(i));
    		addedPlayers.put(newPlayers.get(i), p);
    	}
    	return addedPlayers;
    }
}
