package magic.data;

import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;

import magic.swiss.TieBreakers;

public class TournamentStatus {

    private final int currentRound;
    private final int numberOfRounds;
    private final boolean complete;
    private final NavigableSet<Round> rounds;
    private final NavigableSet<TieBreakers> finalStandings;

    public TournamentStatus(
                            @JsonProperty("currentRound") int currentRound,
                            @JsonProperty("numberOfRounds") int numberOfRounds,
                            @JsonProperty("complete") boolean complete,
                            @JsonProperty("rounds") NavigableSet<Round> rounds,
                            @JsonProperty("finalStandings") NavigableSet<TieBreakers> finalStandings) {
        this.currentRound = currentRound;
        this.numberOfRounds = numberOfRounds;
        this.complete = complete;
        this.rounds = rounds;
        this.finalStandings = finalStandings;
    }
    public int getCurrentRound() {
        return currentRound;
    }
    public int getNumberOfRounds() {
        return numberOfRounds;
    }
    public boolean isComplete() {
        return complete;
    }
    public NavigableSet<Round> getRounds() {
        return rounds;
    }
    public NavigableSet<TieBreakers> finalStandings() {
        return finalStandings;
    }
}
