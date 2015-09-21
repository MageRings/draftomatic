package magic.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Match implements Comparable<Match> {

    public static final int POINTS_FOR_MATCH_WIN = 3;
    public static final int POINTS_FOR_MATCH_LOSS = 0;
    public static final int POINTS_FOR_MATCH_DRAW = 1;

    private final Pairing pairing;
    private final Result result;
    private final boolean p1Drop;
    private final boolean p2Drop;

    public Match(@JsonProperty("pairing") Pairing pairing,
                 @JsonProperty("result") Result result,
                 @JsonProperty("p1Drop") boolean p1Drop,
                 @JsonProperty("p2Drop") boolean p2Drop) {
        this.pairing = pairing;
        this.result = result;
        this.p1Drop = p1Drop;
        this.p2Drop = p2Drop;
    }

    public Pairing getPairing() {
        return pairing;
    }

    public Result getResult() {
        return result;
    }

    public boolean isP1Drop() {
        return p1Drop;
    }

    public boolean isP2Drop() {
        return p2Drop;
    }

    @JsonIgnore
    public int getGamePointsForPlayer(Player player) {
        if (pairing.isPlayer1(player)) {
            return result.getP1Wins() * POINTS_FOR_MATCH_WIN + result.getP2Wins() * POINTS_FOR_MATCH_LOSS
                    + result.getDraws() * POINTS_FOR_MATCH_DRAW;
        } else {
            return result.getP2Wins() * POINTS_FOR_MATCH_WIN + result.getP1Wins() * POINTS_FOR_MATCH_LOSS
                    + result.getDraws() * POINTS_FOR_MATCH_DRAW;
        }
    }

    @JsonIgnore
    public int getPointsForPlayer(Player player) {
        if (pairing.isPlayer1(player)) {
            switch (result.determineOutcome()) {
            case P1_WIN:
                return POINTS_FOR_MATCH_WIN;
            case P1_LOSS:
                return POINTS_FOR_MATCH_LOSS;
            case DRAW:
                return POINTS_FOR_MATCH_DRAW;
            default:
                throw new IllegalStateException();
            }
        } else {
            switch (result.determineOutcome()) {
            case P1_WIN:
                return POINTS_FOR_MATCH_LOSS;
            case P1_LOSS:
                return POINTS_FOR_MATCH_WIN;
            case DRAW:
                return POINTS_FOR_MATCH_DRAW;
            default:
                throw new IllegalStateException();
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
        return result.compareTo(o.result);
    }

    @Override
    public String toString() {
        return "Match [pairing=" + pairing + ", result=" + result + ", p1Drop=" + p1Drop + ", p2Drop=" + p2Drop + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (p1Drop ? 1231 : 1237);
        result = prime * result + (p2Drop ? 1231 : 1237);
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
        if (p1Drop != other.p1Drop) {
            return false;
        }
        if (p2Drop != other.p2Drop) {
            return false;
        }
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
