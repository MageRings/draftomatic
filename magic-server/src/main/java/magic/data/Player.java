package magic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Player implements Comparable<Player> {

    private final String name;

    @JsonCreator
    public Player(@JsonProperty("name") String name) {
        this.name = Preconditions.checkNotNull(name, "Name cannot be null!");
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Player [name=" + name + "]";
    }

    @Override
    public int compareTo(Player o) {
        if (o == null) {
            return 1;
        }
        return this.name.compareTo(o.name);
    }
}
