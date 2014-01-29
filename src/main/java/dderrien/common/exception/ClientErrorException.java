package dderrien.common.exception;

import java.util.logging.Logger;

import javax.ws.rs.core.Response.Status;

public class ClientErrorException extends javax.ws.rs.ClientErrorException {

    private static final long serialVersionUID = 3379216311066858293L;

    private static final Logger logger = Logger.getLogger(ClientErrorException.class.getName());

    public ClientErrorException() {
        super(Status.BAD_REQUEST);
        logger.finest("Exception: " + getClass().getName() + " without message nor cause");
    }

    public ClientErrorException(String message) {
        super(message, Status.BAD_REQUEST);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message);
    }

    public ClientErrorException(Throwable cause) {
        super(Status.BAD_REQUEST, cause);
        logger.finest("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public ClientErrorException(String message, Throwable cause) {
        super(message, Status.BAD_REQUEST, cause);
        logger.finest("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName()
                + ", and cause message: " + cause.getMessage());
    }
}