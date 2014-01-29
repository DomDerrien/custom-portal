package dderrien.common.exception;

import java.util.logging.Logger;

public class NotFoundException extends javax.ws.rs.NotFoundException {

    private static final long serialVersionUID = 3379216311066858297L;

    private static final Logger logger = Logger.getLogger(NotFoundException.class.getName());

    public NotFoundException() {
        super();
        logger.finest("Exception: " + getClass().getName() + " without message nor cause");
    }

    public NotFoundException(String message) {
        super(message);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
        logger.finest("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName()
                + ", and cause message: " + cause.getMessage());
    }
}