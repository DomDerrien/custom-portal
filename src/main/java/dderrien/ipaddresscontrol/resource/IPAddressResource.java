package dderrien.ipaddresscontrol.resource;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import dderrien.common.util.MailConnector;
import dderrien.ipaddresscontrol.model.IPAddress;
import dderrien.ipaddresscontrol.service.IPAddressService;

@Path("/api/ipAddress")
public class IPAddressResource {

	private IPAddressService service;

	@Inject
	public IPAddressResource(IPAddressService service) {
		this.service = service;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getEmitterIPAddress(@Context HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
		// Get the remote address
		String ipAddress = request.getRemoteAddr();
		
		// Compare it to the latest one stored
		List<IPAddress> ipAddresses = service.selectSilent(null, null, Arrays.asList(new String[] { "-reportDate" }));
		if (ipAddresses.size() == 0 || !ipAddresses.get(0).getIpAddress().equals(ipAddress)) {

			// Send an e-mail about it
			MailConnector.sendMailMessage("dom.derrien@gmail.com",  "Dom Derrien",  "Public IP address updated", "Public IP address is now: " + ipAddress, Locale.US);
			// Note: if the email sending fails, the new IP address is not saved now. However it will be saved the next time if the email sendojng succeed.
			
			// Persist the new IP address
			IPAddress newReport = new IPAddress();
			newReport.setIpAddress(ipAddress);
			newReport.setReportDate(new Date());
			service.create(newReport);
		}

		return ipAddress;
	}
}