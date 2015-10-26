package magic.data.tournament;

import java.time.ZonedDateTime;
import java.util.NavigableSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private final ZonedDateTime startTime;
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime endTime;
    // the results alone are mutable
    private final NavigableSet<Round> rounds;

    public TournamentData(@JsonProperty("id") String id,
                          @JsonProperty("numberOfRounds") int numberOfRounds,
                          @JsonProperty("input") TournamentInput input,
                          @JsonProperty("startTime") ZonedDateTime startTime,
                          @JsonProperty("endTime") ZonedDateTime endTime,
                          @JsonProperty("rounds") NavigableSet<Round> rounds) {
        super();
        this.id = id;
        this.numberOfRounds = numberOfRounds;
        this.input = input;
        this.rounds = rounds;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((input == null) ? 0 : input.hashCode());
        result = prime * result + numberOfRounds;
        result = prime * result + ((rounds == null) ? 0 : rounds.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
        if (endTime == null) {
            if (other.endTime != null) {
                return false;
            }
        } else if (!endTime.equals(other.endTime)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (input == null) {
            if (other.input != null) {
                return false;
            }
        } else if (!input.equals(other.input)) {
            return false;
        }
        if (numberOfRounds != other.numberOfRounds) {
            return false;
        }
        if (rounds == null) {
            if (other.rounds != null) {
                return false;
            }
        } else if (!rounds.equals(other.rounds)) {
            return false;
        }
        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }
}
