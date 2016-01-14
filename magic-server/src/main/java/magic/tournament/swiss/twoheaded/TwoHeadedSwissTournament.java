package magic.tournament.swiss.twoheaded;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import magic.data.Pairing;
import magic.data.Player;
import magic.data.database.Database;
import magic.data.tournament.TournamentData;
import magic.data.tournament.TournamentInput;
import magic.tournament.AbstractTournament;
import magic.tournament.TieBreakers;
import magic.tournament.swiss.PlayerData;
import magic.tournament.swiss.SwissPairingCalculator;
import magic.tournament.swiss.SwissTournament;
import magic.tournament.swiss.TournamentState;

public class TwoHeadedSwissTournament extends AbstractTournament {

    private final SwissPairingCalculator calculator;
    private final Map<Player, Player> teams = Maps.newHashMap();

    // used when rehydrating from the DB
    public TwoHeadedSwissTournament(Database db,
                           TournamentData data,
                           SwissPairingCalculator calculator) {
        super(db, data);
        this.calculator = calculator;
        List<Player> players = data.getInput().getPlayers();
        if (players.size() % 2 != 0) {
        	throw new IllegalArgumentException("Got " + players.size() +
        			" players, but expected an event number for a 2hg tournament.");
        }
    	// we assume the initial list of players is sent with the pairs adjacent
        for (int i = 0; i < players.size(); i += 2) {
        	// combine two separate players into a single entity
        	Player player1 = players.get(i);
        	Player player2 = players.get(i + 1);
        	Player combined = new Player(i + 1, player1.getName() + " and " + player2.getName());
        	teams.put(player1, combined);
        	teams.put(player2, combined);
        }
        teams.put(Player.BYE, Player.BYE);
    }

    public TwoHeadedSwissTournament(Database db,
                           String tournamentId,
                           TournamentInput input,
                           Optional<Integer> numberOfRounds,
                           SwissPairingCalculator calculator) {
        this(
                db,
                new TournamentData(
                        tournamentId,
                        numberOfRounds.isPresent() ? numberOfRounds.get()
                            : SwissTournament.getDefaultNumberOfRounds(input.getPlayers().size()/2),
                        input,
                        ZonedDateTime.now(),
                        null,
                        Sets.newTreeSet()),
                calculator);
    }

    @Override
    protected NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                           Map<Player, TieBreakers> tieBreakers) {
    	TournamentState thgState = convertToTHGState(state);
    	Map<Player, TieBreakers> thgBreakers = convertToTHGTieBreakers(tieBreakers);
        NavigableSet<Pairing> thgPairings = this.calculator.innerCalculatePairings(thgState,
        		SwissTournament.rankPlayers(thgState.getPlayers(), thgBreakers));
        return convertToRegularPairings(thgPairings);
    }

	private TournamentState convertToTHGState(TournamentState state) {
		Set<PlayerData> thgData = Sets.newHashSet();
		for (PlayerData pd : state.getRoundData().values()) {
			Set<Player> alreadyMatchedTHG = pd.getAlreadyMatched().stream().map(p -> teams.get(p)).collect(Collectors.toSet());
			PlayerData replacement = new PlayerData(teams.get(pd.getPlayer()), pd.getPoints(), alreadyMatchedTHG);
			thgData.add(replacement);
		}
		if (thgData.size() % 2 != 0) {
			Set<Player> alreadyHaveByes = state.getRoundData().values().stream()
					.filter(d -> d.getAlreadyMatched().contains(Player.BYE))
					.map(d -> teams.get(d.getPlayer()))
					.collect(Collectors.toSet());
			thgData.add(new PlayerData(Player.BYE, 0, alreadyHaveByes));
		}
		return new TournamentState(thgData);
	}

    private Map<Player, TieBreakers> convertToTHGTieBreakers(Map<Player, TieBreakers> tieBreakers) {
    	return tieBreakers.entrySet().stream().collect(Collectors.toMap(e -> teams.get(e.getKey()), e -> e.getValue(), (a, b) -> a));
	}

	private NavigableSet<Pairing> convertToRegularPairings(NavigableSet<Pairing> thgPairings) {
		NavigableSet<Pairing> result = Sets.newTreeSet();
		ListMultimap<Player, Player> available = ArrayListMultimap.create();
		for (Entry<Player, Player> e : teams.entrySet()) {
			available.put(e.getValue(), e.getKey());
		}
		for (Pairing p : thgPairings) {
			List<Player> team2 = available.get(p.getPlayer2());
			if (p.getPlayer1().equals(Player.BYE)) {
				result.add(new Pairing(Player.BYE, team2.get(0), p.getTotalPoints()));
				result.add(new Pairing(Player.BYE, team2.get(1), p.getTotalPoints()));
			} else {
				List<Player> team1 = available.get(p.getPlayer1());
				result.add(new Pairing(team1.get(0), team2.get(0), p.getTotalPoints()));
				result.add(new Pairing(team1.get(1), team2.get(1), p.getTotalPoints()));
			}
		}
		return result;
	}
}
