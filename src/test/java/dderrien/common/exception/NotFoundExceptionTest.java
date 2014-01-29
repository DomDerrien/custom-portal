package dderrien.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.NotFoundException;

public class NotFoundExceptionTest {

    @Test
    public void testDefaultConstructor() {
        new NotFoundException();
    }

    @Test
    public void testConstructorWithMessage() {
        NotFoundException ex = new NotFoundException("test");
        assertEquals("test", ex.getMessage());
    }

    @Test
    public void testConstructorWithCause() {
        NotFoundException ex = new NotFoundException(new NullPointerException("nested"));
        assertEquals("nested", ex.getCause().getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        NotFoundException ex = new NotFoundException("test", new NullPointerException("nested"));
        assertEquals("test", ex.getMessage());
        assertEquals("nested", ex.getCause().getMessage());
    }
}
