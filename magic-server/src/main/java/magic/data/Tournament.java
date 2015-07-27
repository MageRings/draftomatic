package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

public class Tournament {

    private final Format format;
    private final Optional<String> formatCode;

    @JsonCreator
    public Tournament(@JsonProperty("format") Format format,
                      @JsonProperty("code") Optional<String> formatCode) {
        this.format = format;
        this.formatCode = formatCode;
    }

    public Format getFormat() {
        return format;
    }

    public Optional<String> getFormatCode() {
        return formatCode;
    }
}
