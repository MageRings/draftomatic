package magic.data.tournament;

import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;

import magic.data.Round;

/*
 * This class represents the entire persisted state of the tournament.  Everything
 * tracked about the tournament should be contained within.  Note that no information
 * should be duplicated in this object (derived fields can be found in TournamentStatus)
 */
public class TournamentData {

    private final String              id;
    private final int                 numberOfRounds;
    private final TournamentInput     input;
    // the results alone are mutable
    private final NavigableSet<Round> rounds;

    public TournamentData(@JsonProperty("id") String id,
                          @JsonProperty("numberOfRounds") int numberOfRounds,
                          @JsonProperty("input") TournamentInput input,
                          @JsonProperty("rounds") NavigableSet<Round> rounds) {
        super();
        this.id = id;
        this.numberOfRounds = numberOfRounds;
        this.input = input;
        this.rounds = rounds;
    }

    public String getId() {
        return this.id;
    }

    public int getNumberOfRounds() {
        return this.numberOfRounds;
    }

    public TournamentInput getInput() {
        return this.input;
    }

    public NavigableSet<Round> getRounds() {
        return this.rounds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.input == null) ? 0 : this.input.hashCode());
        result = prime * result + this.numberOfRounds;
        result = prime * result + ((this.rounds == null) ? 0 : this.rounds.hashCode());
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
        TournamentData other = (TournamentData) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.input == null) {
            if (other.input != null) {
                return false;
            }
        } else if (!this.input.equals(other.input)) {
            return false;
        }
        if (this.numberOfRounds != other.numberOfRounds) {
            return false;
        }
        if (this.rounds == null) {
            if (other.rounds != null) {
                return false;
            }
        } else if (!this.rounds.equals(other.rounds)) {
            return false;
        }
        return true;
    }
}
