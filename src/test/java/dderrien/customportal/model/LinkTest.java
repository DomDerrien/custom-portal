package dderrien.customportal.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LinkTest {

	@Test
	public void testConstructor() {
		new Link();
	}

	@Test
	public void testAccessors() {
		Long categoryId = 12345L;
		String href = "http://test.com";
		Long ownerId = 45678L;
		String title = "title";

		Link entity = new Link();
		
		entity.setCategoryId(categoryId);
		entity.setHRef(href);
		entity.setOwnerId(ownerId);
		entity.setTitle(title);

		assertEquals(categoryId, entity.getCategoryId());
		assertEquals(href, entity.getHRef());
		assertEquals(ownerId, entity.getOwnerId());
		assertEquals(title, entity.getTitle());
	}
}
