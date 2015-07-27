package magic.swiss;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import magic.data.Format;
import magic.data.Player;

public class SwissManager {

    ConcurrentMap<String, SwissTournament> runningTournaments = Maps.newConcurrentMap();

    public String registerTournament(Format format, Optional<String> formatCode, List<Player> players) {
        if (players == null || players.size() == 0) {
            throw new IllegalArgumentException("Must have at least one player to have a tournament!");
        }
        String uuid = UUID.randomUUID().toString();
        while (runningTournaments.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        runningTournaments.put(uuid, new SwissTournament(players));
        return uuid;
    }

    public SwissTournament getTournament(String tournamentId) {
        return runningTournaments.get(tournamentId);
    }
}
