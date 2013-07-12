package dderrien.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ServerErrorException extends WebApplicationException {

    private static final long serialVersionUID = -5535291983649471185L;

    private static final Logger logger = Logger.getLogger(ServerErrorException.class.getName());

    public ServerErrorException(String message) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ExceptionData(ExceptionData.Type.SERVER_ERROR, message)).type(MediaType.APPLICATION_JSON).build());
        logger.log(Level.SEVERE, "Exception: " + getClass().getName() + " with message: " + message, this);
    }

    public ServerErrorException(Throwable cause) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ExceptionData(ExceptionData.Type.SERVER_ERROR, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.log(Level.SEVERE, "Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage(), this);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ExceptionData(ExceptionData.Type.SERVER_ERROR, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.log(Level.SEVERE, "Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: " + cause.getMessage(), this);
    }
}