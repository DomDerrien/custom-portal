package dderrien.ipaddresscontrol.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class IPAddressTest {

	@Test
	public void testConstructor() {
		new IPAddress();
	}

	@Test
	public void testAccessors() {
		String ipAddress = "10.0.2.2";
		Date report = new Date(123L);

		IPAddress entity = new IPAddress();
		
		entity.setIpAddress(ipAddress);
		entity.setReportDate(report);
		
		assertEquals(ipAddress, entity.getIpAddress());
		assertEquals(report, entity.getReportDate());
	}
}
