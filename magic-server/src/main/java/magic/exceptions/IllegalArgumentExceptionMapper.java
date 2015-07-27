package magic.exceptions;

import javax.ws.rs.core.Response.Status;

public class IllegalArgumentExceptionMapper extends AbstractExceptionMapper<IllegalArgumentException> {

    public IllegalArgumentExceptionMapper() {
        super(Status.BAD_REQUEST);
    }

}
