package magic.exceptions;

import javax.ws.rs.core.Response.Status;

public class TournamentNotFoundExceptionMapper extends AbstractExceptionMapper<TournamentNotFoundException> {

    public TournamentNotFoundExceptionMapper() {
        super(Status.NOT_FOUND);
    }

}
