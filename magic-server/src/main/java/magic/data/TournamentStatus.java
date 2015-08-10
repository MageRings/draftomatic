package magic.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TournamentStatus {

    private final int currentRound;
    private final boolean complete;

    public TournamentStatus(@JsonProperty("currentRound") int currentRound, @JsonProperty("complete") boolean complete) {
        this.currentRound = currentRound;
        this.complete = complete;
    }
    public int getCurrentRound() {
        return currentRound;
    }
    public boolean isComplete() {
        return complete;
    }
}
