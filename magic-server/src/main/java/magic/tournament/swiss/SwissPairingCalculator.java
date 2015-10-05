package magic.tournament.swiss;

import java.util.Map;
import java.util.NavigableSet;

import magic.data.Pairing;
import magic.data.Player;

public interface SwissPairingCalculator {

    NavigableSet<Pairing> innerCalculatePairings(TournamentState state, Map<Player, Integer> playerRankings);

}
