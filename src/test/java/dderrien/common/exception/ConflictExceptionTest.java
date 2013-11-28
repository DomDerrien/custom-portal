package dderrien.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.ConflictException;

public class ConflictExceptionTest {

	@Test
	public void testDefaultConstructor() {
		new ConflictException();
	}

	@Test
	public void testConstructorWithMessage() {
		ConflictException ex = new ConflictException("test");
		assertEquals("test", ex.getMessage());
	}

	@Test
	public void testConstructorWithCause() {
		ConflictException ex = new ConflictException(new NullPointerException("nested"));
		assertEquals("nested", ex.getCause().getMessage());
	}

	@Test
	public void testConstructorWithMessageAndCause() {
		ConflictException ex = new ConflictException("test", new NullPointerException("nested"));
		assertEquals("test", ex.getMessage());
		assertEquals("nested", ex.getCause().getMessage());
	}
}
