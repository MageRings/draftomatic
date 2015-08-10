package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

public class Tournament {

    private final TournamentType format;
    private final Optional<String> formatCode;

    @JsonCreator
    public Tournament(@JsonProperty("format") TournamentType format,
                      @JsonProperty("code") Optional<String> formatCode) {
        this.format = format;
        this.formatCode = formatCode;
    }

    public TournamentType getFormat() {
        return format;
    }

    public Optional<String> getFormatCode() {
        return formatCode;
    }
}
