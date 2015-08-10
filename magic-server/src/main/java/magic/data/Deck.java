package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Deck {
    private final String colors;
    private final String archetype;
    private final TournamentType format;

    @JsonCreator
    public Deck(@JsonProperty("colors") String colors,
                @JsonProperty("archetype") String archetype,
                @JsonProperty("format") TournamentType format) {
        this.colors = colors;
        this.archetype = archetype;
        this.format = format;
    }

    public String getColors() {
        return colors;
    }

    public String getArchetype() {
        return archetype;
    }

    public TournamentType getFormat() {
        return format;
    }
}
