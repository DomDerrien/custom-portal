package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ConflictException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858293L;

    private static final Logger logger = Logger.getLogger(ConflictException.class.getName());

    public ConflictException() {
        super(Response.status(Status.CONFLICT).entity(new ExceptionData(ExceptionData.Type.CONFLICT)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " without message nor cause");
    }

    public ConflictException(String message) {
        super(Response.status(Status.CONFLICT).entity(new ExceptionData(ExceptionData.Type.CONFLICT, message)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message);
    }

    public ConflictException(Throwable cause) {
        super(Response.status(Status.CONFLICT).entity(new ExceptionData(ExceptionData.Type.CONFLICT, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public ConflictException(String message, Throwable cause) {
        super(Response.status(Status.CONFLICT).entity(new ExceptionData(ExceptionData.Type.CONFLICT, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}