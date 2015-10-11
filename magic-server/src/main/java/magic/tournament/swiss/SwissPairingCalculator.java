package magic.tournament.swiss;

import java.util.LinkedHashMap;
import java.util.NavigableSet;

import magic.data.Pairing;
import magic.data.Player;

public interface SwissPairingCalculator {

    NavigableSet<Pairing> innerCalculatePairings(TournamentState state, LinkedHashMap<Player, Integer> playerRankings);

}
