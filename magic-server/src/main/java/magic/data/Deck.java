package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import magic.data.database.FileSystemDB;

public class Deck {

    private final long id;
    private final String colors;
    private final String archetype;
    private final Format format;

    @JsonCreator
    public Deck(@JsonProperty("colors") String colors,
                @JsonProperty("archetype") String archetype,
                @JsonProperty("format") Format format) {
        this(FileSystemDB.nextDeckId(), colors, archetype, format);
    }

    @JsonCreator
    public Deck(@JsonProperty("id") long id,
                @JsonProperty("colors") String colors,
                @JsonProperty("archetype") String archetype,
                @JsonProperty("format") Format format) {
        this.id = id;
        this.colors = colors;
        this.archetype = archetype;
        this.format = format;
    }

    public long getId() {
        return id;
    }

    public String getColors() {
        return colors;
    }

    public String getArchetype() {
        return archetype;
    }

    public Format getFormat() {
        return format;
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
        Deck other = (Deck) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Deck [id=" + id + ", colors=" + colors + ", archetype=" + archetype + ", format=" + format + "]";
    }

}
