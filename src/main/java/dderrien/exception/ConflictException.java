package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class ConflictException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858294L;

    private static final Logger logger = Logger.getLogger(ConflictException.class.getName());

    public ConflictException() {
    	super(Status.CONFLICT);
        logger.finest("Exception: " + getClass().getName() + " without message nor cause");
    }

    public ConflictException(String message) {
    	super(message, Status.CONFLICT);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message);
    }

    public ConflictException(Throwable cause) {
    	super(cause, Status.CONFLICT);
        logger.finest("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public ConflictException(String message, Throwable cause) {
    	super(message, cause, Status.CONFLICT);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}