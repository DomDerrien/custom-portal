package dderrien.common.exception;

import java.util.logging.Logger;

public class UnauthorizedException extends javax.ws.rs.NotAuthorizedException {

    private static final long serialVersionUID = 3379216311066858292L;

    private static final Logger logger = Logger.getLogger(UnauthorizedException.class.getName());

    public UnauthorizedException(String message) {
        super(message);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
        logger.finest("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName()
                + ", and cause message: " + cause.getMessage());
    }
}