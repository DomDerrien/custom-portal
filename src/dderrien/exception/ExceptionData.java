package dderrien.exception;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExceptionData {

    public enum Type {
        CLIENT_ERROR, CONFLICT, NOT_FOUND, NOT_MODIFIED, SERVER_ERROR, UNAUTHORIZED, NO_CONTENT, TIMEOUT, NOT_SATISFIABLE_RANGE,
    };

    Type type;
    String message;
    String cause;

    public ExceptionData(Type type) {
        this.type = type;
    }

    public ExceptionData(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public ExceptionData(Type type, Throwable cause) {
        this.type = type;
        this.cause = cause.getMessage();
    }

    public ExceptionData(Type type, String message, Throwable cause) {
        this(type, message);
        this.cause = cause.getMessage();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}