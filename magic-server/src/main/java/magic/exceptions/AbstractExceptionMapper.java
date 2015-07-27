package magic.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractExceptionMapper.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Status status;

    @Context
    private HttpServletRequest request;

    public AbstractExceptionMapper(Status status) {
        this.status = status;
    }

    @Override
    public final Response toResponse(T exception) {
        log.error(exception.getMessage());
        log.debug("Exception stacktrace", exception);

        ResponseBuilder builder = Response.status(status);
        builder.type(MediaType.APPLICATION_JSON);

        try {
            builder.entity(MAPPER.writeValueAsString(exception.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to write out error:" + e);
        }
        return builder.build();
    }
}
