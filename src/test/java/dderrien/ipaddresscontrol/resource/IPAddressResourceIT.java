package dderrien.ipaddresscontrol.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

public class IPAddressResourceIT {

	@Test
	public void testGetIPAddressOK() throws ClientProtocolException, IOException {
		
		try(CloseableHttpClient client = HttpClients.createDefault()) {

			HttpGet get = new HttpGet("http://localhost:9090/api/ipAddress");
			get.setHeader("Accept", "text/plain");

			try(CloseableHttpResponse response = client.execute(get)) {

				assertEquals(Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());

				HttpEntity entity = response.getEntity();
				assertNotNull(entity);
				
				try(InputStream is = entity.getContent()) {
					byte[] content = new byte[256];
					int byteNb = is.read(content);
					String ipAddress = new String(content, 0, byteNb);
					
					assertTrue(ipAddress.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"));
				}
			}
		}
	}

	@Test
	public void testGetIPAddressWrongFormat() throws ClientProtocolException, IOException {
		
		try(CloseableHttpClient client = HttpClients.createDefault()) {

			HttpGet get = new HttpGet("http://localhost:9090/api/ipAddress");
			get.setHeader("Accept", "application/json");

			try(CloseableHttpResponse response = client.execute(get)) {

				assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatusLine().getStatusCode());
			}
		}
	}
}
