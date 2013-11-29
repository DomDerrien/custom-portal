package dderrien.ipaddresscontrol.service;

import org.junit.Test;

import dderrien.ipaddresscontrol.dao.IPAddressDao;

public class IPAddressServiceTest {

	@Test
	public void testConstructor() {
		new IPAddressService(new IPAddressDao());
	}
}
