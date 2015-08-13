package magic.data;

import javax.annotation.CheckForNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Match implements Comparable<Match> {

    public static final int POINTS_FOR_MATCH_WIN = 3;
    public static final int POINTS_FOR_MATCH_LOSS = 0;
    public static final int POINTS_FOR_MATCH_DRAW = 1;

    private final Pairing pairing;
    private final Result result;

    public Match(@JsonProperty("pairing") Pairing pairing,
            @JsonProperty("result") Result result) {
        this.pairing = pairing;
        this.result = result;
    }

    public Pairing getPairing() {
        return pairing;
    }

    @CheckForNull
    public Result getResult() {
        return result;
    }

    @JsonIgnore
    public int getGamePointsForPlayer(Player player) {
        if (pairing.isPlayer1(player)) {
            return result.getP1Wins() * POINTS_FOR_MATCH_WIN + result.getP2Wins() * POINTS_FOR_MATCH_LOSS + result.getDraws() * POINTS_FOR_MATCH_DRAW;
        } else {
            return result.getP2Wins() * POINTS_FOR_MATCH_WIN + result.getP1Wins() * POINTS_FOR_MATCH_LOSS + result.getDraws() * POINTS_FOR_MATCH_DRAW;
        }
    }

    @JsonIgnore
    public int getPointsForPlayer(Player player) {
        if (pairing.isPlayer1(player)) {
            switch (result.determineOutcome()) {
            case P1_WIN: return POINTS_FOR_MATCH_WIN;
            case P1_LOSS: return POINTS_FOR_MATCH_LOSS;
            case DRAW: return POINTS_FOR_MATCH_DRAW;
            default: throw new IllegalStateException();
            }
        } else {
            switch (result.determineOutcome()) {
            case P1_WIN: return POINTS_FOR_MATCH_LOSS;
            case P1_LOSS: return POINTS_FOR_MATCH_WIN;
            case DRAW: return POINTS_FOR_MATCH_DRAW;
            default: throw new IllegalStateException();
            }
        }
    }

    @Override
    public int compareTo(Match o) {
        if (o == null) {
            return 1;
        }
        int compare = pairing.compareTo(o.pairing);
        if (compare != 0) {
            return compare;
        }
        if (result == null && o.result == null) {
            return 0;
        }
        if (result == null) {
            return -1;
        }
        return result.compareTo(o.result);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pairing == null) ? 0 : pairing.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
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
        Match other = (Match) obj;
        if (pairing == null) {
            if (other.pairing != null) {
                return false;
            }
        } else if (!pairing.equals(other.pairing)) {
            return false;
        }
        if (result == null) {
            if (other.result != null) {
                return false;
            }
        } else if (!result.equals(other.result)) {
            return false;
        }
        return true;
    }

}
