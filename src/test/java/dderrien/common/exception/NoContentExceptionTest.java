package dderrien.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.NoContentException;

public class NoContentExceptionTest {

	@Test
	public void testDefaultConstructor() {
		new NoContentException();
	}

	@Test
	public void testConstructorWithMessage() {
		NoContentException ex = new NoContentException("test");
		assertEquals("test", ex.getMessage());
	}

	@Test
	public void testConstructorWithCause() {
		NoContentException ex = new NoContentException(new NullPointerException("nested"));
		assertEquals("nested", ex.getCause().getMessage());
	}

	@Test
	public void testConstructorWithMessageAndCause() {
		NoContentException ex = new NoContentException("test", new NullPointerException("nested"));
		assertEquals("test", ex.getMessage());
		assertEquals("nested", ex.getCause().getMessage());
	}
}
