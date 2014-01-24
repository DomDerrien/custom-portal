package dderrien.common.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.googlecode.objectify.Key;

import dderrien.common.dao.AbstractDao;
import dderrien.common.exception.UnauthorizedException;
import dderrien.common.model.AbstractAuthBase;
import dderrien.common.model.AbstractBase;
import dderrien.common.model.User;
import dderrien.common.util.Range;

public class AbstractAuthServiceTest {

	public class TestModel extends AbstractAuthBase<TestModel> {
		String test;
		public String getTest() { return test; }
		public void setTest(String test) { this.test = test; }
	};
	public class TestDao extends AbstractDao<TestModel> {};

	@Test
	public void testConstructor() {
		new AbstractAuthService<TestModel>(new TestDao(), mock(UserService.class)) {};
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectForAdmin() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);
		List<TestModel> selection = Arrays.asList(new TestModel[] { new TestModel() });
		when(dao.select(null, null, null)).thenReturn(selection);

		assertEquals(selection, service.select(null, null, null));
		verify(userService, times(1)).isLoggedAdmin();
		verify(dao, times(1)).select(null, null, null);
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithoutFilter() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);
		
		final List<TestModel> selection = Arrays.asList(new TestModel[] { new TestModel() });
		doAnswer(new Answer<List<TestModel>>() {
			@Override
			public List<TestModel> answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
				assertEquals(1, filters.size());
				assertEquals(id, filters.get("ownerId"));
				return selection;
			}
		}).when(dao).select(anyMap(), any(Range.class), anyList());

		assertEquals(selection, service.select(null, null, null));
		verify(userService, times(1)).isLoggedAdmin();
		verify(userService, times(1)).getLoggedUser();
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithFilter() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);
		
		final Map<String, Object> filters = new HashMap<>();
		filters.put("ownerId", id);
		filters.put("name", "test");
		
		final List<TestModel> selection = Arrays.asList(new TestModel[] { new TestModel() });
		doAnswer(new Answer<List<TestModel>>() {
			@Override
			public List<TestModel> answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
				assertEquals(2, filters.size());
				assertEquals(id, filters.get("ownerId"));
				assertEquals("test", filters.get("name"));
				return selection;
			}
		}).when(dao).select(filters, null, null);

		assertEquals(selection, service.select(filters, null, null));
		verify(userService, times(1)).isLoggedAdmin();
		verify(userService, times(2)).getLoggedUser();
		verify(dao, times(1)).select(filters, null, null);
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithFilterWithoutOwnerId() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);
		
		final Map<String, Object> filters = new HashMap<>();
		filters.put("name", "test");
		
		final List<TestModel> selection = Arrays.asList(new TestModel[] { new TestModel() });
		doAnswer(new Answer<List<TestModel>>() {
			@Override
			public List<TestModel> answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
				assertEquals(2, filters.size());
				assertEquals(id, filters.get("ownerId"));
				assertEquals("test", filters.get("name"));
				return selection;
			}
		}).when(dao).select(filters, null, null);

		assertEquals(selection, service.select(filters, null, null));
		verify(userService, times(1)).isLoggedAdmin();
		verify(userService, times(1)).getLoggedUser();
		verify(dao, times(1)).select(filters, null, null);
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}
	
	@Test(expected = UnauthorizedException.class)
	public void testSelectWithFilterAndOtherOwnerId() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);
		
		final Map<String, Object> filters = new HashMap<>();
		filters.put("ownerId", id * 2);

		service.select(filters, null, null);
	}
	
	@Test
	public void testGetForAdmin() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);

		final Long id = 12345L;
		final Long version = 4567L;
		TestModel candidate = new TestModel();
		candidate.setVersion(version + 1); // Stored version is older, no NotModifiedException thrown then
		when(dao.get(id)).thenReturn(candidate);

		assertEquals(candidate, service.get(id, version));
		verify(userService, times(1)).isLoggedAdmin();
		verify(dao, times(1)).get(id);
		verify(dao, times(1)).get(anyLong());
	}
	
	@Test
	public void testGetByOwner() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);
		
		final Long version = 4567L;
		TestModel candidate = new TestModel();
		candidate.setOwnerId(id);
		candidate.setVersion(version + 1); // Stored version is older, no NotModifiedException thrown then
		when(dao.get(id)).thenReturn(candidate);

		assertEquals(candidate, service.get(id, version));
		verify(userService, times(1)).isLoggedAdmin();
		verify(userService, times(1)).getLoggedUser();
		verify(dao, times(1)).get(id);
		verify(dao, times(1)).get(anyLong());
	}
	
	@Test(expected = UnauthorizedException.class)
	public void testGetByNonOwner() {
		TestDao dao = mock(TestDao.class);
		when(dao.getModelClass()).thenReturn(TestModel.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);
		
		final Long version = 4567L;
		TestModel candidate = new TestModel();
		candidate.setOwnerId(id * 2);
		candidate.setVersion(version + 1); // Stored version is older, no NotModifiedException thrown then
		when(dao.get(id)).thenReturn(candidate);

		service.get(id, version);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testCreateForAdmin() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);

		TestModel candidate = new TestModel();
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.save(candidate)).thenReturn(key);

		assertEquals(key, service.create(candidate));
		verify(userService, times(1)).isLoggedAdmin();
		verify(dao, times(1)).save(candidate);
		verify(dao, times(1)).save(any(TestModel.class));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testCreateByOwner() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);

		TestModel candidate = new TestModel();
		candidate.setOwnerId(id);
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.save(candidate)).thenReturn(key);

		assertEquals(key, service.create(candidate));
		verify(userService, times(1)).isLoggedAdmin();
		verify(userService, times(1)).getLoggedUser();
		verify(dao, times(1)).save(candidate);
		verify(dao, times(1)).save(any(TestModel.class));
	}
	
	@Test(expected = UnauthorizedException.class)
	@SuppressWarnings("unchecked")
	public void testCreateByNonOwner() {
		TestDao dao = mock(TestDao.class);
		when(dao.getModelClass()).thenReturn(TestModel.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);

		TestModel candidate = new TestModel();
		candidate.setOwnerId(id * 2);
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.save(candidate)).thenReturn(key);

		service.create(candidate);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateForAdmin() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);

		final Long id = 12345L;
		final Long version = 4567L;
		TestModel original = new TestModel();
		original.setId(id);
		original.setVersion(version);
		TestModel candidate = new TestModel();
		candidate.setId(id);
		candidate.setTest("test");
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.get(id)).thenReturn(original).thenReturn(candidate);
		when(dao.save(original)).thenReturn(key);
		when(key.getId()).thenReturn(id);

		assertEquals(candidate, service.update(id, version, candidate));
		verify(userService, times(2)).isLoggedAdmin();
		verify(dao, times(2)).get(id);
		verify(dao, times(1)).save(original);
		verify(dao, times(1)).save(any(TestModel.class));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateByOwner() {
		TestDao dao = mock(TestDao.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		final Long version = 4567L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);

		TestModel original = new TestModel();
		original.setId(id);
		original.setVersion(version);
		original.setOwnerId(id);
		TestModel candidate = new TestModel();
		candidate.setId(id);
		candidate.setTest("test");
		candidate.setOwnerId(id);
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.get(id)).thenReturn(original).thenReturn(candidate);
		when(dao.save(original)).thenReturn(key);
		when(key.getId()).thenReturn(id);

		assertEquals(candidate, service.update(id, version, candidate));
		verify(userService, times(2)).isLoggedAdmin();
		verify(userService, times(2)).getLoggedUser();
		verify(dao, times(2)).get(id);
		verify(dao, times(1)).save(original);
		verify(dao, times(1)).save(any(TestModel.class));
	}
	
	@Test(expected = UnauthorizedException.class)
	@SuppressWarnings("unchecked")
	public void testUpdateByNonOwner() {
		TestDao dao = mock(TestDao.class);
		when(dao.getModelClass()).thenReturn(TestModel.class);
		UserService userService = mock(UserService.class);
		AbstractAuthService<TestModel> service = new AbstractAuthService<TestModel>(dao, userService) {};

		final Long id = 12345L;
		final Long version = 4567L;
		User user = mock(User.class);
		when(userService.isLoggedAdmin()).thenReturn(Boolean.FALSE);
		when(userService.getLoggedUser()).thenReturn(user);
		when(user.getId()).thenReturn(id);

		TestModel original = new TestModel();
		original.setId(id);
		original.setVersion(version);
		original.setOwnerId(id);
		TestModel candidate = new TestModel();
		candidate.setId(id);
		candidate.setOwnerId(id * 2);
		candidate.setTest("test");
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.get(id)).thenReturn(original).thenReturn(candidate);
		when(dao.save(original)).thenReturn(key);
		when(key.getId()).thenReturn(id);

		service.update(id, version, candidate);
	}
}
