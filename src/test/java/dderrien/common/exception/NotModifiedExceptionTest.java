package dderrien.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.NotModifiedException;

public class NotModifiedExceptionTest {

    @Test
    public void testDefaultConstructor() {
        new NotModifiedException();
    }

    @Test
    public void testConstructorWithMessage() {
        NotModifiedException ex = new NotModifiedException("test");
        assertEquals("test", ex.getMessage());
    }

    @Test
    public void testConstructorWithCause() {
        NotModifiedException ex = new NotModifiedException(new NullPointerException("nested"));
        assertEquals("nested", ex.getCause().getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        NotModifiedException ex = new NotModifiedException("test", new NullPointerException("nested"));
        assertEquals("test", ex.getMessage());
        assertEquals("nested", ex.getCause().getMessage());
    }
}
