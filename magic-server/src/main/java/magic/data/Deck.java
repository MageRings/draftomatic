package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Deck {

    private final int id;
    private final String colors;
    private final String archetype;
    private final TournamentType format;

    @JsonCreator
    public Deck(@JsonProperty("id") int id,
                @JsonProperty("colors") String colors,
                @JsonProperty("archetype") String archetype,
                @JsonProperty("format") TournamentType format) {
        this.id = id;
        this.colors = colors;
        this.archetype = archetype;
        this.format = format;
    }

    public int getId() {
        return id;
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
        Deck other = (Deck) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

}
