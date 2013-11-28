package dderrien.common.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import dderrien.common.dao.UserDao;
import dderrien.common.exception.ConflictException;
import dderrien.common.exception.NotFoundException;
import dderrien.common.exception.UnauthorizedException;
import dderrien.common.model.User;
import dderrien.common.util.Range;

public class UserServiceTest {

	@Test
	public void testConstructor() {
		new UserService(mock(UserDao.class));
	}

	@Test
	public void testGetSystemUserService() {
		assertNotNull(new UserService(mock(UserDao.class)).getSystemUserService());
	}

	@Test(expected = UnauthorizedException.class)
	public void testGetLoggedUserNone() {
		UserService service = spy(new UserService(mock(UserDao.class)));
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.getCurrentUser()).thenReturn(null);
		
		service.getLoggedUser();
	}

	@Test(expected = NotFoundException.class)
	@SuppressWarnings("unchecked")
	public void testGetLoggedUserUnknown() {
		UserDao dao = mock(UserDao.class);
		UserService service = spy(new UserService(dao));
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		com.google.appengine.api.users.User systemUser = new com.google.appengine.api.users.User("email", "domain");
		
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.getCurrentUser()).thenReturn(systemUser);
		doAnswer(new Answer<List<User>>() {
			@Override
			public List<User> answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
				assertEquals(1, filters.size());
				assertEquals("email", filters.get("email"));
				return new ArrayList<User>();
			}
			
		}).when(dao).select(anyMap(), any(Range.class), anyList());
		
		service.getLoggedUser();
	}

	@Test(expected = ConflictException.class)
	@SuppressWarnings("unchecked")
	public void testGetLoggedUserEmailUnicityBroken() {
		UserDao dao = mock(UserDao.class);
		UserService service = spy(new UserService(dao));
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		com.google.appengine.api.users.User systemUser = new com.google.appengine.api.users.User("email", "domain");
		
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.getCurrentUser()).thenReturn(systemUser);
		doAnswer(new Answer<List<User>>() {
			@Override
			public List<User> answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
				assertEquals(1, filters.size());
				assertEquals("email", filters.get("email"));
				return Arrays.asList(new User[] { new User(), new User() });
			}
			
		}).when(dao).select(anyMap(), any(Range.class), anyList());
		
		service.getLoggedUser();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetLoggedUserSuccess() {
		UserDao dao = mock(UserDao.class);
		UserService service = spy(new UserService(dao));
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		com.google.appengine.api.users.User systemUser = new com.google.appengine.api.users.User("email", "domain");
		final User loggedUser = mock(User.class);
		
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.getCurrentUser()).thenReturn(systemUser);
		doAnswer(new Answer<List<User>>() {
			@Override
			public List<User> answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
				assertEquals(1, filters.size());
				assertEquals("email", filters.get("email"));
				return Arrays.asList(new User[] { loggedUser });
			}
			
		}).when(dao).select(anyMap(), any(Range.class), anyList());
		
		assertEquals(loggedUser, service.getLoggedUser());
		verify(service, times(1)).getSystemUserService();
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}
	
	@Test
	public void testIsLoggedAdmin() {
		UserDao dao = mock(UserDao.class);
		UserService service = spy(new UserService(dao));
		com.google.appengine.api.users.UserService systemUserService = mock(com.google.appengine.api.users.UserService.class);
		
		when(service.getSystemUserService()).thenReturn(systemUserService);
		when(systemUserService.isUserAdmin()).thenReturn(Boolean.TRUE);
		
		assertTrue(service.isLoggedAdmin());
	}
}
