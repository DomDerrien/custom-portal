package dderrien.common.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;

public class InvalidRangeException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858295L;

    private static final Logger logger = Logger.getLogger(InvalidRangeException.class.getName());

    public InvalidRangeException() {
        super(416);
        logger.finest("Exception: " + getClass().getName() + " without message nor cause");
    }

    public InvalidRangeException(String message) {
        super(message, 416);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message);
    }

    public InvalidRangeException(Throwable cause) {
        super(cause, 416);
        logger.finest("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public InvalidRangeException(String message, Throwable cause) {
        super(message, cause, 416);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName()
                + ", and cause message: " + cause.getMessage());
    }
}