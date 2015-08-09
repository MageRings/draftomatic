package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class Player implements Comparable<Player> {

    public static final Player BYE = new Player();

    private final String name;
    private final int id;

    private Player() {
        this.name = "BYE";
        this.id = 0;
    }

    @JsonCreator
    public Player(@JsonProperty("name") String name,
                  @JsonProperty("id") int id) {
        Preconditions.checkArgument(isValidName(name), "Invalid name: " + name);
        Preconditions.checkArgument(id > 0, "Player id must be greater than 0.");

        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
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
        Player other = (Player) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Player [name=" + name + ", id=" + id + "]";
    }

    @Override
    public int compareTo(Player o) {
        if (o == null) {
            return 1;
        }
        return Integer.compare(id, o.id);
    }

    private static boolean isValidName(String name) {
        return !(Strings.isNullOrEmpty(name) || name.equalsIgnoreCase("bye"));
    }
}
