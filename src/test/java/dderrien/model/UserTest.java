package dderrien.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserTest {

	@Test
	public void testConstructor() {
		new User();
	}

	@Test
	public void testAccessors() {
		String name = "name";
		String email = "email";
		
		User entity = new User();
		entity.setName(name);
		entity.setEmail(email);
		
		assertEquals(name, entity.getName());
		assertEquals(email, entity.getEmail());
	}
}
