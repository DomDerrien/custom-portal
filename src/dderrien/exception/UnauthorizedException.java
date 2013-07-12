package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class UnauthorizedException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858293L;

    private static final Logger logger = Logger.getLogger(UnauthorizedException.class.getName());

    public UnauthorizedException() {
        super(Response.status(Status.UNAUTHORIZED).entity(new ExceptionData(ExceptionData.Type.UNAUTHORIZED)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " without message nor cause");
    }

    public UnauthorizedException(String message) {
        super(Response.status(Status.UNAUTHORIZED).entity(new ExceptionData(ExceptionData.Type.UNAUTHORIZED, message)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message);
    }

    public UnauthorizedException(Throwable cause) {
        super(Response.status(Status.UNAUTHORIZED).entity(new ExceptionData(ExceptionData.Type.UNAUTHORIZED, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(Response.status(Status.UNAUTHORIZED).entity(new ExceptionData(ExceptionData.Type.UNAUTHORIZED, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}