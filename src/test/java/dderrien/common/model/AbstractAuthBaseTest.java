package dderrien.common.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dderrien.common.exception.ClientErrorException;

public class AbstractAuthBaseTest {

	public class TestModel extends AbstractAuthBase<TestModel> {};

	@Test
	public void testConstructor() {
		new TestModel();
	}

	@Test
	public void testAccessors() {
		Long ownerId = 12345L;
		
		TestModel entity = new TestModel();
		entity.setOwnerId(ownerId);
		
		assertEquals(ownerId, entity.getOwnerId());
	}

	@Test(expected = ClientErrorException.class)
	public void testPrePersistWithoutOwnerId() {
		new TestModel().checkOwnerId();
	}

	@Test(expected = ClientErrorException.class)
	public void testPrePersistWithZeroOwnerId() {
		TestModel entity = new TestModel();
		entity.setOwnerId(0L);
		entity.checkOwnerId();
	}

	@Test
	public void testPrePersistValid() {
		TestModel entity = new TestModel();
		entity.setOwnerId(12345L);
		entity.checkOwnerId();
	}
}
