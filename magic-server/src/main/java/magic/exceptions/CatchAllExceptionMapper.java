package magic.exceptions;

import javax.ws.rs.core.Response.Status;

public class CatchAllExceptionMapper extends AbstractExceptionMapper<Throwable> {

    public CatchAllExceptionMapper() {
        super(Status.INTERNAL_SERVER_ERROR);
    }

}
