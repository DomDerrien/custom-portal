package dderrien.common.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class NotModifiedException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858298L;

    private static final Logger logger = Logger.getLogger(NotModifiedException.class.getName());

    public NotModifiedException() {
        super(Status.NOT_MODIFIED);
        logger.finest("Exception: " + getClass().getName() + " without message nor cause");
    }

    public NotModifiedException(String message) {
        super(message, Status.NOT_MODIFIED);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message);
    }

    public NotModifiedException(Throwable cause) {
        super(cause, Status.NOT_MODIFIED);
        logger.finest("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public NotModifiedException(String message, Throwable cause) {
        super(message, cause, Status.NOT_MODIFIED);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}