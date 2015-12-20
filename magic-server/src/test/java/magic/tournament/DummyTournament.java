package magic.tournament;

import java.util.Map;
import java.util.NavigableSet;

import jersey.repackaged.com.google.common.collect.Sets;
import magic.data.Pairing;
import magic.data.Player;
import magic.data.database.NoopDB;
import magic.data.tournament.TournamentData;
import magic.tournament.swiss.TournamentState;

/**
 * Dummy tournament used for testing methods on abstract tournament
 *
 */
public class DummyTournament extends AbstractTournament {

    public DummyTournament(TournamentData data) {
        super(NoopDB.NOOPDB, data);
    }

    @Override
    protected NavigableSet<Pairing> innerCalculatePairings(TournamentState state,
                                                           Map<Player, TieBreakers> tieBreakers) {
        return Sets.newTreeSet();
    }

}
