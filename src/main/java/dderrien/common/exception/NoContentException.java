package dderrien.common.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class NoContentException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858296L;

    private static final Logger logger = Logger.getLogger(NoContentException.class.getName());

    public NoContentException() {
        super(Status.NO_CONTENT);
        logger.finest("Exception: " + getClass().getName() + " without message nor cause");
    }

    public NoContentException(String message) {
        super(message, Status.NO_CONTENT);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message);
    }

    public NoContentException(Throwable cause) {
        super(cause, Status.NO_CONTENT);
        logger.finest("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public NoContentException(String message, Throwable cause) {
        super(message, cause, Status.NO_CONTENT);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}