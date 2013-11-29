package dderrien.customportal.service;

import org.junit.Test;

import dderrien.common.dao.UserDao;
import dderrien.common.service.UserService;
import dderrien.customportal.dao.LinkDao;

public class LinkServiceTest {

	@Test
	public void testConstructor() {
		new LinkService(new LinkDao(), new UserService(new UserDao()));
	}
}
