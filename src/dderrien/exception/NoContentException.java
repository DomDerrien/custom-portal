package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class NoContentException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858293L;

    private static final Logger logger = Logger.getLogger(NoContentException.class.getName());

    public NoContentException() {
        super(Response.status(Status.NO_CONTENT).entity(new ExceptionData(ExceptionData.Type.NO_CONTENT)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " without message nor cause");
    }

    public NoContentException(String message) {
        super(Response.status(Status.NO_CONTENT).entity(new ExceptionData(ExceptionData.Type.NO_CONTENT, message)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message);
    }

    public NoContentException(Throwable cause) {
        super(Response.status(Status.NO_CONTENT).entity(new ExceptionData(ExceptionData.Type.NO_CONTENT, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public NoContentException(String message, Throwable cause) {
        super(Response.status(Status.NO_CONTENT).entity(new ExceptionData(ExceptionData.Type.NO_CONTENT, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}