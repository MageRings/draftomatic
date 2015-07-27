package magic.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

    public static final int POINTS_FOR_MATCH_WIN = 3;
    public static final int POINTS_FOR_MATCH_LOSS = 0;
    public static final int POINTS_FOR_MATCH_DRAW = 1;

    enum Outcome {
        P1_WIN,
        P1_LOSS,
        DRAW
    }

    private final Pairing pairing;
    private final int p1Wins;
    private final int p2Wins;
    private final int draws;

    public Result(@JsonProperty("pairing") Pairing pairing,
                  @JsonProperty("p1Wins") int p1Wins,
                  @JsonProperty("p2Wins") int p2Wins,
                  @JsonProperty("draws") int draws) {
        this.pairing = pairing;
        this.p1Wins = p1Wins;
        this.p2Wins = p2Wins;
        this.draws = draws;
    }

    public Pairing getPairing() {
        return pairing;
    }

    public int getP1Wins() {
        return p1Wins;
    }

    public int getP2Wins() {
        return p2Wins;
    }

    public int getDraws() {
        return draws;
    }

    @JsonIgnore
    public int getPointsForPlayer(Player player) {
        if (player.equals(pairing.getPlayer1())) {
            switch (determineOutcome()) {
            case P1_WIN: return POINTS_FOR_MATCH_WIN;
            case P1_LOSS: return POINTS_FOR_MATCH_LOSS;
            case DRAW: return POINTS_FOR_MATCH_DRAW;
            }
        }
        if (player.equals(pairing.getPlayer2())) {
            switch (determineOutcome()) {
            case P1_WIN: return POINTS_FOR_MATCH_LOSS;
            case P1_LOSS: return POINTS_FOR_MATCH_WIN;
            case DRAW: return POINTS_FOR_MATCH_DRAW;
            }
        }
        throw new IllegalArgumentException("Player " + player + " was not involved in this match!");
    }

    @JsonIgnore
    public Outcome determineOutcome() {
        if (p1Wins > p2Wins) {
            return Outcome.P1_WIN;
        }
        if (p2Wins > p1Wins) {
            return Outcome.P1_LOSS;
        }
        return Outcome.DRAW;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + draws;
        result = prime * result + p1Wins;
        result = prime * result + p2Wins;
        result = prime * result + ((pairing == null) ? 0 : pairing.hashCode());
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
        Result other = (Result) obj;
        if (draws != other.draws) {
            return false;
        }
        if (p1Wins != other.p1Wins) {
            return false;
        }
        if (p2Wins != other.p2Wins) {
            return false;
        }
        if (pairing == null) {
            if (other.pairing != null) {
                return false;
            }
        } else if (!pairing.equals(other.pairing)) {
            return false;
        }
        return true;
    }

}
