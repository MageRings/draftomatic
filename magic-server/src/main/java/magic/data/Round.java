package magic.data;

import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Round implements Comparable<Round> {

    private final int number;
    private final NavigableSet<Match> matches;

    public Round(
            @JsonProperty("number") int number,
            @JsonProperty("matches") NavigableSet<Match> matches) {
        this.number = number;
        this.matches = matches;
    }

    public int getNumber() {
        return number;
    }
    public NavigableSet<Match> getMatches() {
        return matches;
    }

    @Override
    public int compareTo(Round other) {
        return Integer.compare(number, other.number);
    }

    @Override
    public String toString() {
        return "Round [number=" + number + ", matches=" + matches + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + number;
        result = prime * result + ((matches == null) ? 0 : matches.hashCode());
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
        if (number != other.number) {
            return false;
        }
        if (matches == null) {
            if (other.matches != null) {
                return false;
            }
        } else if (!matches.equals(other.matches)) {
            return false;
        }
        return true;
    }

}
