package magic.tournament.swiss;

import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

import com.google.common.collect.Multimap;

import magic.data.Pairing;
import magic.data.Player;

public interface SwissPairingCalculator {

    NavigableSet<Pairing> innerCalculatePairings(Multimap<Integer, Player> playersAtEachPointLevel,
                                                 NavigableMap<Player, Integer> playerRankings,
                                                 Map<Player, Integer> pointsPerPlayer,
                                                 Multimap<Player, Player> alreadyMatched);

}
