package dderrien.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.ClientErrorException;

public class ClientErrorExceptionTest {

    @Test
    public void testDefaultConstructor() {
        new ClientErrorException();
    }

    @Test
    public void testConstructorWithMessage() {
        ClientErrorException ex = new ClientErrorException("test");
        assertEquals("test", ex.getMessage());
    }

    @Test
    public void testConstructorWithCause() {
        ClientErrorException ex = new ClientErrorException(new NullPointerException("nested"));
        assertEquals("nested", ex.getCause().getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        ClientErrorException ex = new ClientErrorException("test", new NullPointerException("nested"));
        assertEquals("test", ex.getMessage());
        assertEquals("nested", ex.getCause().getMessage());
    }
}
