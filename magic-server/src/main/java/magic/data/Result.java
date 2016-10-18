package magic.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Result implements Comparable<Result> {

    public static enum Outcome {
        P1_WIN, P1_LOSS, DRAW
    }

    public static final Result INCOMPLETE = new Result(0, 0, 0);
    public static final Result BYE = new Result(0, 2, 0);

    private final int p1Wins;
    private final int p2Wins;
    private final int draws;

    public Result(@JsonProperty("p1Wins") int p1Wins,
                  @JsonProperty("p2Wins") int p2Wins,
                  @JsonProperty("draws") int draws) {
        this.p1Wins = p1Wins;
        this.p2Wins = p2Wins;
        this.draws = draws;
    }

    public int getP1Wins() {
        return this.p1Wins;
    }

    public int getP2Wins() {
        return this.p2Wins;
    }

    public int getDraws() {
        return this.draws;
    }

    @JsonIgnore
    public Outcome determineOutcome() {
        if (this.p1Wins > this.p2Wins) {
            return Outcome.P1_WIN;
        }
        if (this.p2Wins > this.p1Wins) {
            return Outcome.P1_LOSS;
        }
        return Outcome.DRAW;
    }

    @JsonIgnore
    public int getNumberOfGames() {
        return this.p1Wins + this.p2Wins + this.draws;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.draws;
        result = prime * result + this.p1Wins;
        result = prime * result + this.p2Wins;
        return result;
    }

    @Override
    public String toString() {
        return "Result [p1Wins=" + this.p1Wins + ", p2Wins=" + this.p2Wins + ", draws=" + this.draws + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Result other = (Result) obj;
        if (this.draws != other.draws) {
            return false;
        }
        if (this.p1Wins != other.p1Wins) {
            return false;
        }
        if (this.p2Wins != other.p2Wins) {
            return false;
        }
        return true;
    }

    // best results sorts first
    @Override
    public int compareTo(Result o) {
        if (o == null) {
            return -1;
        }
        if (this.p1Wins != o.p1Wins) {
            return Integer.compare(o.p1Wins, this.p1Wins);
        }
        if (this.p2Wins != o.p2Wins) {
            return Integer.compare(o.p2Wins, this.p2Wins);
        }
        return Integer.compare(o.draws, this.draws);
    }

}
