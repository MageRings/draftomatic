package magic.data.tournament;

import java.util.List;
import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import magic.data.Player;
import magic.tournament.TieBreakers;

/*
 * This is a class with derived fields useful for the frontend.  Note that it is not persisted.
 */
public class TournamentStatus {

    private final int                       currentRound;
    private final boolean                   complete;
    private final TournamentData            tournamentData;
    private final NavigableSet<TieBreakers> finalStandings;
    private final List<Player> seatings;

    public TournamentStatus(
                            @JsonProperty("currentRound") final int currentRound,
                            @JsonProperty("complete") final boolean complete,
                            @JsonProperty("tournamentData") final TournamentData tournamentData,
                            @JsonProperty("finalStandings") final NavigableSet<TieBreakers> finalStandings,
                            @JsonProperty("seatings") final List<Player> seatings
    ) {
        this.currentRound = currentRound;
        this.complete = complete;
        this.tournamentData = tournamentData;
        this.finalStandings = finalStandings;
        this.seatings = seatings;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean isComplete() {
        return complete;
    }

    public TournamentData getTournamentData() {
        return tournamentData;
    }

    public NavigableSet<TieBreakers> getFinalStandings() {
        return finalStandings;
    }

    public List<Player> getSeatings() {return seatings;}
}
