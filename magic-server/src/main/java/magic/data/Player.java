package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import magic.data.database.FileSystemDB;

public class Player implements Comparable<Player> {

    public static final Player BYE = new Player();

    private final long   id;
    private final String name;

    private Player() {
        this.id = 0;
        this.name = "BYE";
    }

    public Player(String name) {
        this(FileSystemDB.nextPlayerId(), name);
    }

    @JsonCreator
    public Player(@JsonProperty("id") long id,
                  @JsonProperty("name") String name) {
        Preconditions.checkArgument(id > -1, "Player id must be greater than -1.");
        Preconditions.checkArgument(isValidName(name), "Invalid name: " + name);

        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int compareTo(Player o) {
        if (o == null) {
            return 1;
        }
        return Long.compare(this.id, o.id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
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
        Player other = (Player) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Player [id=" + this.id + ", name=" + this.name + "]";
    }

    private static boolean isValidName(String name) {
        return !(Strings.isNullOrEmpty(name));
    }
}
