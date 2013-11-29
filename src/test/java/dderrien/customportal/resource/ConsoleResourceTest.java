package dderrien.customportal.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.googlecode.objectify.Key;

import dderrien.common.exception.NotFoundException;
import dderrien.common.model.AbstractBase;
import dderrien.common.model.User;
import dderrien.common.service.UserService;
import dderrien.common.util.MailConnector;

public class ConsoleResourceTest {

    private static LocalServiceTestHelper  helper;

    @BeforeClass
    public static void setUpBeforeClass() {
        MailConnector.setMockLogger(mock(Logger.class));
        helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig());
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
		new ConsoleResource(mock(UserService.class));
	}

	@Test
	public void testGetLoginURL() {
		String resourceURL = "/path";
		String loginURL = "login(/path)";
		UserService service = mock(UserService.class);
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.createLoginURL(resourceURL)).thenReturn(loginURL);
		assertEquals(loginURL, new ConsoleResource(service).getLoginURL(resourceURL));
	}

	@Test
	public void testGetLogoutURL() {
		String resourceURL = "/path";
		String logoutURL = "logout(/path)";
		UserService service = mock(UserService.class);
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.createLogoutURL(resourceURL)).thenReturn(logoutURL);
		assertEquals(logoutURL, new ConsoleResource(service).getLogoutURL(resourceURL));
	}

	@Test
	public void testPrepareConsoleForNonAuthenticated() throws ServletException, IOException, URISyntaxException {
		UserService service = mock(UserService.class);
		ConsoleResource resource = new ConsoleResource(service);
		
		String uri = "/path";
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.createLoginURL(uri)).thenReturn("login(/path)");

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getUserPrincipal()).thenReturn(null);
		
		Response automaticResponse = resource.prepareConsole(request, response);
		assertEquals(Status.TEMPORARY_REDIRECT.getStatusCode(), automaticResponse.getStatus());
		assertEquals("login(/path)", automaticResponse.getLocation().toString());
	}

	@Test
	public void testPrepareConsoleForKnownAuthenticaed() throws ServletException, IOException, URISyntaxException {
		UserService service = mock(UserService.class);
		ConsoleResource resource = new ConsoleResource(service);
		
		String uri = "/path";
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.createLoginURL(uri)).thenReturn("login(/path)");
		when(systemUserService.createLogoutURL("login(/path)")).thenReturn("logout(login(/path))");

		Principal connectedUser = mock(Principal.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		User savedUser = mock(User.class);
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getUserPrincipal()).thenReturn(connectedUser);
		when(service.getLoggedUser()).thenReturn(savedUser);
		when(service.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(request.getRequestDispatcher("/WEB-INF/templates/console.jsp")).thenReturn(dispatcher);

		assertNull(resource.prepareConsole(request, response));
		verify(request, times(1)).setAttribute("user", savedUser);
		verify(request, times(1)).setAttribute("logoutURL", "logout(login(/path))");
		verify(request, times(2)).setAttribute(anyString(), anyList());
		verify(dispatcher, times(1)).forward(request, response);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPrepareConsoleForUnknownAuthenticaed() throws ServletException, IOException, URISyntaxException {
		UserService service = mock(UserService.class);
		ConsoleResource resource = new ConsoleResource(service);
		
		String uri = "/path";
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		com.google.appengine.api.users.User systemUser = new com.google.appengine.api.users.User("test@testcom", "test.com");
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.createLoginURL(uri)).thenReturn("login(/path)");
		when(systemUserService.createLogoutURL("login(/path)")).thenReturn("logout(login(/path))");
		when(systemUserService.getCurrentUser()).thenReturn(systemUser);

		Principal connectedUser = mock(Principal.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Key<AbstractBase<User>> key = mock(Key.class);
		Long id = 12345L;
		User savedUser = mock(User.class);
		List<User> selection = new ArrayList<User>();
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		
		when(request.getRequestURI()).thenReturn(uri);
		when(request.getUserPrincipal()).thenReturn(connectedUser);
		when(service.getLoggedUser()).thenThrow(NotFoundException.class);
		when(service.create(any(User.class))).thenReturn(key);
		when(key.getId()).thenReturn(id);
		when(service.get(id)).thenReturn(savedUser);
		when(service.isLoggedAdmin()).thenReturn(Boolean.TRUE);
		when(service.selectSilent(null, null, null)).thenReturn(selection);
		when(request.getRequestDispatcher("/WEB-INF/templates/console.jsp")).thenReturn(dispatcher);

		assertNull(resource.prepareConsole(request, response));
		verify(request, times(1)).setAttribute("user", savedUser);
		verify(request, times(1)).setAttribute("logoutURL", "logout(login(/path))");
		verify(request, times(1)).setAttribute("users", selection);
		verify(request, times(3)).setAttribute(anyString(), anyList());
		verify(dispatcher, times(1)).forward(request, response);
	}
}
