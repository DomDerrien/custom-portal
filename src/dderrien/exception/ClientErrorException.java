package dderrien.exception;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ClientErrorException extends WebApplicationException {

    private static final long serialVersionUID = 3379216311066858293L;

    private static final Logger logger = Logger.getLogger(ClientErrorException.class.getName());

    public ClientErrorException() {
        super(Response.status(Status.BAD_REQUEST).entity(new ExceptionData(ExceptionData.Type.CLIENT_ERROR)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " without message nor cause");
    }

    public ClientErrorException(String message) {
        super(Response.status(Status.BAD_REQUEST).entity(new ExceptionData(ExceptionData.Type.CLIENT_ERROR, message)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message);
    }

    public ClientErrorException(Throwable cause) {
        super(Response.status(Status.BAD_REQUEST).entity(new ExceptionData(ExceptionData.Type.CLIENT_ERROR, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with cause: " + cause.getClass().getName() + " and cause message: " + cause.getMessage());
    }

    public ClientErrorException(String message, Throwable cause) {
        super(Response.status(Status.BAD_REQUEST).entity(new ExceptionData(ExceptionData.Type.CLIENT_ERROR, message, cause)).type(MediaType.APPLICATION_JSON).build());
        logger.severe("Exception: " + getClass().getName() + " with message: " + message + ", with cause: " + cause.getClass().getName() + ", and cause message: "
                + cause.getMessage());
    }
}