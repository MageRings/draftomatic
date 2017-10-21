package magic.tournament;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jersey.repackaged.com.google.common.collect.Sets;
import magic.data.Player;
import magic.data.database.Database;
import magic.data.tournament.TournamentInput;
import magic.exceptions.TournamentNotFoundException;
import magic.tournament.swiss.GraphPairing;
import magic.tournament.swiss.SwissTournament;
import magic.tournament.swiss.twoheaded.TwoHeadedSwissTournament;

public final class TournamentManager {

    private final ConcurrentMap<String, Tournament> runningTournaments = Maps.newConcurrentMap();
    private final Database db;

    public TournamentManager(Database db) {
    	this.db = db;
        try {
            this.db.loadTournaments().forEach((id, data) -> {
            	Tournament t;
            	switch(data.getInput().getFormat()) {
            	case LIMITED_2HG_DRAFT:
            	case LIMITED_2HG_SEALED:
            		t = new TwoHeadedSwissTournament(this.db, data, new GraphPairing());
            		break;
            	default:
            		t= new SwissTournament(this.db, data, new GraphPairing());
            		break;
            	}
            	this.runningTournaments.put(id, t);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String registerTournament(TournamentInput input, Optional<Integer> numberOfRounds) {
        if (input.getPlayers().size() == 0) {
            throw new IllegalArgumentException("Must have at least one player to have a tournament!");
        }
        String uuid = UUID.randomUUID().toString();
        while (this.runningTournaments.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        Tournament t;
        switch(input.getFormat()) {
        case LIMITED_2HG_DRAFT:
        case LIMITED_2HG_SEALED:
        	t = new TwoHeadedSwissTournament(this.db, uuid, registerPlayers(input), numberOfRounds, new GraphPairing());
        	break;
        default:
        	t = new SwissTournament(this.db, uuid, registerPlayers(input), numberOfRounds, new GraphPairing());
        	break;
        }
        t.initFirstRound();
        this.runningTournaments.put(uuid, t);
        return uuid;
    }

    private TournamentInput registerPlayers(TournamentInput input) {
        List<String> playersToRegister = input.getPlayers().stream().filter(p -> p.getId() <= 0).map(Player::getName)
                .collect(Collectors.toList());
        try {
            Map<String, Player> newPlayers = this.db.registerPlayers(playersToRegister);
            Set<Player> seen = Sets.newHashSet();
            Set<Player> players = this.db.getPlayers();
            List<Player> withIds = Lists.newArrayList();
            input.getPlayers().forEach(p -> {
                if (p.getId() > 0) {
                	if (!players.contains(p)) {
                		throw new IllegalArgumentException("Player " + p.getName() + " is not in the database!");
                	}
                	withIds.add(p);
                	seen.add(p);
                } else {
                	Player player = newPlayers.get(p.getName());
                	withIds.add(player);
                	seen.add(player);
                }
            });
            if (seen.size() != input.getPlayers().size()) {
                throw new IllegalArgumentException("Not all input players are unqiue!");
            }

            return new TournamentInput(input.getFormat(), input.getCode(), withIds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Tournament getTournament(String tournamentId) {
        Tournament tournament = this.runningTournaments.get(tournamentId);
        if (tournament == null) {
            throw new TournamentNotFoundException(tournamentId);
        }
        return tournament;
    }
}
