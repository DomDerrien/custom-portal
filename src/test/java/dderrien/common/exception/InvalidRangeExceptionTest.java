package dderrien.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.InvalidRangeException;

public class InvalidRangeExceptionTest {

    @Test
    public void testDefaultConstructor() {
        new InvalidRangeException();
    }

    @Test
    public void testConstructorWithMessage() {
        InvalidRangeException ex = new InvalidRangeException("test");
        assertEquals("test", ex.getMessage());
    }

    @Test
    public void testConstructorWithCause() {
        InvalidRangeException ex = new InvalidRangeException(new NullPointerException("nested"));
        assertEquals("nested", ex.getCause().getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        InvalidRangeException ex = new InvalidRangeException("test", new NullPointerException("nested"));
        assertEquals("test", ex.getMessage());
        assertEquals("nested", ex.getCause().getMessage());
    }
}
