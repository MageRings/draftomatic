package magic.tournament;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.Optional;

import magic.data.Match;
import magic.data.Round;
import magic.data.tournament.TournamentStatus;

public interface Tournament {

    TournamentStatus getStatus();

    void initFirstRound();
    
    Round undoLastRound();

    Round registerResults(Optional<Integer> roundRequested, Collection<Match> thisRoundResults);

    NavigableSet<TieBreakers> getTieBreakers(Optional<Integer> roundRequested);

}
