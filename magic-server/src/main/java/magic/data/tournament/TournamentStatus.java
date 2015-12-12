package magic.data.tournament;

import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;

import magic.tournament.TieBreakers;

/*
 * This is a class with derived fields useful for the frontend.  Note that it is not persisted.
 */
public class TournamentStatus {

    private final int                       currentRound;
    private final boolean                   complete;
    private final TournamentData            tournamentData;
    private final NavigableSet<TieBreakers> finalStandings;

    public TournamentStatus(
                            @JsonProperty("currentRound") int currentRound,
                            @JsonProperty("complete") boolean complete,
                            @JsonProperty("tournamentData") TournamentData tournamentData,
                            @JsonProperty("finalStandings") NavigableSet<TieBreakers> finalStandings) {
        this.currentRound = currentRound;
        this.complete = complete;
        this.tournamentData = tournamentData;
        this.finalStandings = finalStandings;
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
}
