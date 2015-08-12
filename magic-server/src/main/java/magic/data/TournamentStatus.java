package magic.data;

import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TournamentStatus {

    private final int currentRound;
    private final boolean complete;
    private final NavigableSet<Round> rounds;

    public TournamentStatus(
            @JsonProperty("currentRound") int currentRound,
            @JsonProperty("complete") boolean complete,
            @JsonProperty("rounds") NavigableSet<Round> rounds) {
        this.currentRound = currentRound;
        this.complete = complete;
        this.rounds = rounds;
    }
    public int getCurrentRound() {
        return currentRound;
    }
    public boolean isComplete() {
        return complete;
    }
    public NavigableSet<Round> getRounds() {
        return rounds;
    }
}
