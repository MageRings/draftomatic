package magic.data;

import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Round implements Comparable<Round> {

    private final int number;
    private final NavigableSet<Pairing> pairings;
    private final NavigableSet<Result> results;
    private final boolean complete;

    public Round(
            @JsonProperty("number") int number,
            @JsonProperty("pairings") NavigableSet<Pairing> pairings,
            @JsonProperty("results") NavigableSet<Result> results,
            @JsonProperty("complete") boolean complete) {
        this.number = number;
        this.pairings = pairings;
        this.results = results;
        this.complete = complete;
    }

    public int getNumber() {
        return number;
    }
    public NavigableSet<Pairing> getPairings() {
        return pairings;
    }
    public NavigableSet<Result> getResults() {
        return results;
    }
    public boolean isComplete() {
        return complete;
    }

    @Override
    public int compareTo(Round other) {
        return Integer.compare(number, other.number);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (complete ? 1231 : 1237);
        result = prime * result + number;
        result = prime * result + ((pairings == null) ? 0 : pairings.hashCode());
        result = prime * result + ((results == null) ? 0 : results.hashCode());
        return result;
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
        Round other = (Round) obj;
        if (complete != other.complete) {
            return false;
        }
        if (number != other.number) {
            return false;
        }
        if (pairings == null) {
            if (other.pairings != null) {
                return false;
            }
        } else if (!pairings.equals(other.pairings)) {
            return false;
        }
        if (results == null) {
            if (other.results != null) {
                return false;
            }
        } else if (!results.equals(other.results)) {
            return false;
        }
        return true;
    }

}
