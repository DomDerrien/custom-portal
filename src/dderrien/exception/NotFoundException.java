package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class NotFoundException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858293L;

    private static final Logger logger = Logger.getLogger(NotFoundException.class.getName());

    public NotFoundException() {
        super(Response.status(Status.NOT_FOUND).entity(new ExceptionData(ExceptionData.Type.NOT_FOUND)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " without message nor cause");
    }

    public NotFoundException(String message) {
        super(Response.status(Status.NOT_FOUND).entity(new ExceptionData(ExceptionData.Type.NOT_FOUND, message)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message);
    }

    public NotFoundException(Throwable cause) {
        super(Response.status(Status.NOT_FOUND).entity(new ExceptionData(ExceptionData.Type.NOT_FOUND, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public NotFoundException(String message, Throwable cause) {
        super(Response.status(Status.NOT_FOUND).entity(new ExceptionData(ExceptionData.Type.NOT_FOUND, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}