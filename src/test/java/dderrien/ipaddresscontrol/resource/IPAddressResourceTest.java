package dderrien.ipaddresscontrol.resource;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

import dderrien.common.model.AbstractBase;
import dderrien.ipaddresscontrol.model.IPAddress;
import dderrien.ipaddresscontrol.resource.IPAddressResource;
import dderrien.ipaddresscontrol.service.IPAddressService;
import dderrien.common.util.MailConnector;
import dderrien.common.util.Range;

public class IPAddressResourceTest {

    private static LocalServiceTestHelper  helper;

    @BeforeClass
    public static void setUpBeforeClass() {
        MailConnector.setMockLogger(mock(Logger.class));
        helper = new LocalServiceTestHelper(new LocalMailServiceTestConfig().setLogMailLevel(Level.FINEST));
    }

    @Before
    public void setUp() throws Exception {
    	helper.setUp();
    }

    @After
    public void tearDown() throws Exception {
    	helper.tearDown();
    }

	@Test
	public void testConstructor() {
		new IPAddressResource(mock(IPAddressService.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetKnownIPAddress() throws Exception {
		IPAddressService service = mock(IPAddressService.class);
		IPAddressResource resource = new IPAddressResource(service);
		
		String ipAddress = "10.0.2.2";
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRemoteAddr()).thenReturn(ipAddress);
		IPAddress savedIPAddress = new IPAddress();
		savedIPAddress.setIpAddress(ipAddress);
		
		final List<IPAddress> selection = Arrays.asList(new IPAddress[] { savedIPAddress });
		doAnswer(new Answer<List<IPAddress>>() {
			@Override
			public List<IPAddress> answer(InvocationOnMock invocation) throws Throwable {
				List<String> orders = (List<String>) invocation.getArguments()[2];
				assertEquals(1, orders.size());
				assertEquals("-reportDate", orders.get(0));
				return selection;
			}
			
		}).when(service).selectSilent(anyMap(), any(Range.class), anyList());
		
		assertEquals(ipAddress, resource.getEmitterIPAddress(request));
		verify(service, times(1)).selectSilent(anyMap(), any(Range.class), anyList());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetNoKnownIPAddress() throws Exception {
		IPAddressService service = mock(IPAddressService.class);
		IPAddressResource resource = new IPAddressResource(service);
		
		final String ipAddress = "10.0.2.2";
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRemoteAddr()).thenReturn(ipAddress);
		
		final List<IPAddress> selection = new ArrayList<IPAddress>();
		doAnswer(new Answer<List<IPAddress>>() {
			@Override
			public List<IPAddress> answer(InvocationOnMock invocation) throws Throwable {
				List<String> orders = (List<String>) invocation.getArguments()[2];
				assertEquals(1, orders.size());
				assertEquals("-reportDate", orders.get(0));
				return selection;
			}
			
		}).when(service).selectSilent(anyMap(), any(Range.class), anyList());
		doAnswer(new Answer<Key<AbstractBase<IPAddress>>>() {
			@Override
			public Key<AbstractBase<IPAddress>> answer(InvocationOnMock invocation) throws Throwable {
				IPAddress candidate = (IPAddress) invocation.getArguments()[0];
				assertEquals(ipAddress, candidate.getIpAddress());
				return mock(Key.class);
			}
		}).when(service).create(any(IPAddress.class));
		
		assertEquals(ipAddress, resource.getEmitterIPAddress(request));
		verify(service, times(1)).selectSilent(anyMap(), any(Range.class), anyList());
	}

	@Test(expected = MessagingException.class)
	@SuppressWarnings("unchecked")
	public void testGetUnknownIPAddress() throws Exception {
		IPAddressService service = mock(IPAddressService.class);
		IPAddressResource resource = new IPAddressResource(service);
		
		final String ipAddress = "10.0.2.2";
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getRemoteAddr()).thenReturn(ipAddress);
		IPAddress storedIPAddress = new IPAddress();
		storedIPAddress.setIpAddress("123.435.767");

		final List<IPAddress> selection = Arrays.asList(new IPAddress[] { storedIPAddress, storedIPAddress });
		doAnswer(new Answer<List<IPAddress>>() {
			@Override
			public List<IPAddress> answer(InvocationOnMock invocation) throws Throwable {
				List<String> orders = (List<String>) invocation.getArguments()[2];
				assertEquals(1, orders.size());
				assertEquals("-reportDate", orders.get(0));
				return selection;
			}
			
		}).when(service).selectSilent(anyMap(), any(Range.class), anyList());
		doAnswer(new Answer<Key<AbstractBase<IPAddress>>>() {
			@Override
			public Key<AbstractBase<IPAddress>> answer(InvocationOnMock invocation) throws Throwable {
				IPAddress candidate = (IPAddress) invocation.getArguments()[0];
				assertEquals(ipAddress, candidate.getIpAddress());
				return mock(Key.class);
			}
		}).when(service).create(any(IPAddress.class));
		
        MailConnector.foolNextMessagePost();
		resource.getEmitterIPAddress(request);
	}

	@Test
	public void testGetAnnotations() {
		int annotationNb = 2;
		String methodName = "getEmitterIPAddress";
		Class<IPAddressResource> candidate = IPAddressResource.class;

		Method method = null;
		for (Method m : candidate.getDeclaredMethods()) {
			if (methodName.equals(m.getName()))
				method = m;
		}
		if (method == null)
			fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

		Annotation[] annotations = method.getAnnotations();
		assertEquals(annotationNb, annotations.length);

		// @Get
		int adx = 0; // annotation index
		assertEquals(GET.class, annotations[adx].annotationType());
		// @Produces(MediaType.TEXT_PLAIN)
		adx++;
		assertEquals(Produces.class, annotations[adx].annotationType());
		assertEquals(1, ((Produces) annotations[adx]).value().length);
		assertEquals(MediaType.TEXT_PLAIN, ((Produces) annotations[adx]).value()[0]);
	}
}
