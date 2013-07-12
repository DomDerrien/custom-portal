package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class NotModifiedException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858293L;

    private static final Logger logger = Logger.getLogger(NotModifiedException.class.getName());

    public NotModifiedException() {
        super(Response.status(Status.NOT_MODIFIED).entity(new ExceptionData(ExceptionData.Type.NOT_MODIFIED)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " without message nor cause");
    }

    public NotModifiedException(String message) {
        super(Response.status(Status.NOT_MODIFIED).entity(new ExceptionData(ExceptionData.Type.NOT_MODIFIED, message)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message);
    }

    public NotModifiedException(Throwable cause) {
        super(Response.status(Status.NOT_MODIFIED).entity(new ExceptionData(ExceptionData.Type.NOT_MODIFIED, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public NotModifiedException(String message, Throwable cause) {
        super(Response.status(Status.NOT_MODIFIED).entity(new ExceptionData(ExceptionData.Type.NOT_MODIFIED, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}