package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InvalidRangeException extends WebApplicationException {

    private static final long serialVersionUID = 6916891851312588007L;

    private static final Logger logger = Logger.getLogger(InvalidRangeException.class.getName());

    public InvalidRangeException() {
        super(Response.status(416).entity(new ExceptionData(ExceptionData.Type.NOT_SATISFIABLE_RANGE)).type(MediaType.APPLICATION_JSON).build());
        // No logging as many REST request will generate this exception when no resource have yet been created (no purchase, no chat room for a game, etc.)
    }

    public InvalidRangeException(String message) {
        super(Response.status(416).entity(new ExceptionData(ExceptionData.Type.NOT_SATISFIABLE_RANGE, message)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message);
    }

    public InvalidRangeException(Throwable cause) {
        super(Response.status(416).entity(new ExceptionData(ExceptionData.Type.NOT_SATISFIABLE_RANGE, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public InvalidRangeException(String message, Throwable cause) {
        super(Response.status(416).entity(new ExceptionData(ExceptionData.Type.NOT_SATISFIABLE_RANGE, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}