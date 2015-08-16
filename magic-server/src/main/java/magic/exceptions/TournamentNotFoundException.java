package magic.exceptions;

public class TournamentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4360875430197861038L;

    public TournamentNotFoundException(String tournamentId) {
        super("Tournament " + tournamentId + " does not exist!");
    }
}
