package magic.data;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import magic.data.database.Database;

public class Tournament {

    private final long id;
    private final TournamentType format;
    private final Optional<String> formatCode;

    @JsonCreator
    public Tournament(@JsonProperty("format") TournamentType format,
                      @JsonProperty("code") Optional<String> formatCode) {
        this(Database.nextTournamentId(), format, formatCode);
    }

    @JsonCreator
    public Tournament(@JsonProperty("id") long id,
                      @JsonProperty("format") TournamentType format,
                      @JsonProperty("code") Optional<String> formatCode) {
        this.id = id;
        this.format = format;
        this.formatCode = formatCode;
    }

    public long getId() {
        return id;
    }

    public TournamentType getFormat() {
        return format;
    }

    public Optional<String> getFormatCode() {
        return formatCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        Tournament other = (Tournament) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tournament [id=" + id + ", format=" + format + ", formatCode=" + formatCode + "]";
    }

}
