package dderrien.common.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response.Status;

public class ServerErrorException extends javax.ws.rs.ServerErrorException {

    private static final long serialVersionUID = 3379216311066858299L;

    private static final Logger logger = Logger.getLogger(ServerErrorException.class.getName());

    public ServerErrorException(String message) {
    	super(message, Status.INTERNAL_SERVER_ERROR);
        logger.log(Level.FINEST, "Exception: " + getClass().getName() + " with message: " + message, this);
    }

    public ServerErrorException(Throwable cause) {
    	super(Status.INTERNAL_SERVER_ERROR, cause);
        logger.log(Level.FINEST, "Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage(), this);
    }

    public ServerErrorException(String message, Throwable cause) {
    	super(message, Status.INTERNAL_SERVER_ERROR, cause);
        logger.log(Level.FINEST, "Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: " + cause.getMessage(), this);
    }
}