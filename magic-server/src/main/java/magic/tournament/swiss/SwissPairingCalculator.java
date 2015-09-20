package magic.tournament.swiss;

import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;

import com.google.common.collect.Multimap;

import magic.data.Pairing;
import magic.data.Player;
import magic.tournament.TieBreakers;

public interface SwissPairingCalculator {

    NavigableSet<Pairing> innerCalculatePairings(Multimap<Integer, Player> playersAtEachPointLevel,
                                                 Optional<Map<Player, TieBreakers>> tieBreakers,
                                                 Map<Player, Integer> pointsPerPlayer,
                                                 Multimap<Player, Player> alreadyMatched);

}
