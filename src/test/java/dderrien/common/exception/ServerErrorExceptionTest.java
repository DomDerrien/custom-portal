package dderrien.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.ServerErrorException;

public class ServerErrorExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        ServerErrorException ex = new ServerErrorException("test");
        assertEquals("test", ex.getMessage());
    }

    @Test
    public void testConstructorWithCause() {
        ServerErrorException ex = new ServerErrorException(new NullPointerException("nested"));
        assertEquals("nested", ex.getCause().getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        ServerErrorException ex = new ServerErrorException("test", new NullPointerException("nested"));
        assertEquals("test", ex.getMessage());
        assertEquals("nested", ex.getCause().getMessage());
    }
}
