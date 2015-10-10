package magic.data;

import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Round implements Comparable<Round> {

    private final int number;
    private final NavigableSet<Match> matches;
    private final boolean complete;

    public Round(
            @JsonProperty("number") int number,
            @JsonProperty("complete") boolean complete,
            @JsonProperty("matches") NavigableSet<Match> matches) {
        this.number = number;
        this.matches = matches;
        this.complete = complete;
    }

    public int getNumber() {
        return number;
    }
    public NavigableSet<Match> getMatches() {
        return matches;
    }
    public boolean isComplete() {
        return complete;
    }

    @Override
    public int compareTo(Round other) {
        return Integer.compare(number, other.number);
    }

    @Override
    public String toString() {
        return "Round [number=" + number + ", matches=" + matches + ", complete=" + complete + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (complete ? 1231 : 1237);
        result = prime * result + ((matches == null) ? 0 : matches.hashCode());
        result = prime * result + number;
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
        if (matches == null) {
            if (other.matches != null) {
                return false;
            }
        } else if (!matches.equals(other.matches)) {
            return false;
        }
        if (number != other.number) {
            return false;
        }
        return true;
    }
}
