package magic.data;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tournament {

    private final int id;
    private final TournamentType format;
    private final Optional<String> formatCode;

    @JsonCreator
    public Tournament(@JsonProperty("format") TournamentType format,
                      @JsonProperty("code") Optional<String> formatCode) {
        this(0, format, formatCode);
    }

    @JsonCreator
    public Tournament(@JsonProperty("id") int id,
                      @JsonProperty("format") TournamentType format,
                      @JsonProperty("code") Optional<String> formatCode) {
        this.id = id;
        this.format = format;
        this.formatCode = formatCode;
    }

    public int getId() {
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
        result = prime * result + id;
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

}
