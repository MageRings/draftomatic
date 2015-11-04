package magic.data.tournament;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import magic.data.Format;
import magic.data.Player;

/*
 * This class represents the mandatory data passed in by the user to describe the tournament.
 */
public final class TournamentInput {

    private final Format      format;
    private final String      code;   // refers to the specific set or sets being played
    private final List<Player> players;

    public TournamentInput(
                           @JsonProperty("format") Format format,
                           @JsonProperty("code") String code,
                           @JsonProperty("players") List<Player> players) {
        this.format = format;
        this.code = code;
        this.players = players;
    }

    public Format getFormat() {
        return format;
    }

    public String getCode() {
        return code;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "TournamentInput [format=" + format + ", code=" + code + ", players=" + players + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((players == null) ? 0 : players.hashCode());
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
        TournamentInput other = (TournamentInput) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        if (format != other.format) {
            return false;
        }
        if (players == null) {
            if (other.players != null) {
                return false;
            }
        } else if (!players.equals(other.players)) {
            return false;
        }
        return true;
    }
}
