package dderrien.common.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import dderrien.common.exception.UnauthorizedException;

public class UnauthorizedExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        UnauthorizedException ex = new UnauthorizedException("test");
        assertEquals("HTTP 401 Unauthorized", ex.getMessage()); // Without cause, given message is not reported, only
                                                                // logged by our custom class
    }

    @Test
    public void testConstructorWithCause() {
        UnauthorizedException ex = new UnauthorizedException(new NullPointerException("nested"));
        assertNull(ex.getCause()); // Given cause is not reported, only logged by our custom class
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        UnauthorizedException ex = new UnauthorizedException("test", new NullPointerException("nested"));
        assertEquals("test", ex.getMessage());
        assertNull(ex.getCause()); // Given cause is not reported, only logged by our custom class
    }
}
