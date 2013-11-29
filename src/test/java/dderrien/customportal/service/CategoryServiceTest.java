package dderrien.customportal.service;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.googlecode.objectify.Key;

import dderrien.common.dao.UserDao;
import dderrien.common.exception.ServerErrorException;
import dderrien.common.model.AbstractBase;
import dderrien.common.service.UserService;
import dderrien.common.util.Range;
import dderrien.customportal.dao.CategoryDao;
import dderrien.customportal.model.Category;

public class CategoryServiceTest {

	@Test
	public void testConstructor() {
		new CategoryService(new CategoryDao(), new UserService(new UserDao()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAll() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);
		final List<Category> selection = Arrays.asList(new Category[] { new Category() });
		doAnswer(new Answer<List<Category>>() {
			@Override
			public List<Category> answer(InvocationOnMock invocation) throws Throwable {
				List<String> orders = (List<String>) invocation.getArguments()[2];
				assertEquals(1, orders.size());
				assertEquals("+order", orders.get(0));
				return selection;
			}
			
		}).when(dao).select(anyMap(), any(Range.class), anyList());

		assertEquals(selection, service.select(null, null, null));
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAllWithOrder() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);
		List<String> orders = new ArrayList<String>();
		orders.add("-creation");
		final List<Category> selection = Arrays.asList(new Category[] { new Category() });
		doAnswer(new Answer<List<Category>>() {
			@Override
			public List<Category> answer(InvocationOnMock invocation) throws Throwable {
				List<String> orders = (List<String>) invocation.getArguments()[2];
				assertEquals(2, orders.size());
				assertEquals("-creation", orders.get(0));
				assertEquals("+order", orders.get(1));
				return selection;
			}
			
		}).when(dao).select(anyMap(), any(Range.class), anyList());

		assertEquals(selection, service.select(null, null, orders));
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateWithoutOrder() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);
		
		Long id = 12345L;
		Category existing = new Category();
		existing.setId(id);
		existing.setTitle("existing");
		Category entity = new Category();
		entity.setId(id);
		entity.setTitle("update");
		Key<AbstractBase<Category>> key = mock(Key.class);
		when(dao.save(existing)).thenReturn(key);
		when(key.getId()).thenReturn(id);
		when(dao.get(id)).thenReturn(entity);
		
		assertEquals(entity, service.update(existing, entity));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateOrderNotChanged() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);
		
		Long id = 12345L;
		Long order = 1L; // Second position
		Category existing = new Category();
		existing.setId(id);
		existing.setTitle("existing");
		existing.setOrder(order);
		Category entity = new Category();
		entity.setId(id);
		entity.setTitle("update");
		entity.setOrder(order);
		Key<AbstractBase<Category>> key = mock(Key.class);
		when(dao.save(existing)).thenReturn(key);
		when(key.getId()).thenReturn(id);
		when(dao.get(id)).thenReturn(entity);
		
		assertEquals(entity, service.update(existing, entity));
	}

	private static Category createCandidate(Long id, Long order) {
		Category out = new Category();
		out.setId(id);
		out.setOrder(order);
		return out;
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateOrderMoveFrontToBack() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);

		Long id = 12345L;
		Category existing = createCandidate(id, null);
		Category _1 = createCandidate(1L, 1L);
		Category _2 = createCandidate(2L, 2L);
		List<Category> selection = Arrays.asList(new Category[] { existing, _1, _2 });
		when(dao.select(anyMap(), any(Range.class), anyList())).thenReturn(selection);

		Key<AbstractBase<Category>> key = mock(Key.class);
		when(dao.save(any(Category.class))).thenReturn(key);
		when(key.getId()).thenReturn(1L).thenReturn(2L).thenReturn(id);

		Category update = createCandidate(id, 2L);
		when(dao.get(id)).thenReturn(update);
		assertEquals(update, service.update(existing, update));
		verify(dao, times(3)).get(anyLong());
		verify(dao, times(1)).save(_1);
		verify(dao, times(1)).save(_2);
		verify(dao, times(1)).save(existing);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateOrderPushBack() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);

		Long id = 12345L;
		Category _0 = createCandidate(0L, 0L);
		Category existing = createCandidate(id, 1L);
		Category _2 = createCandidate(2L, 2L);
		Category _3 = createCandidate(3L, 3L);
		Category _4 = createCandidate(4L, 4L);
		List<Category> selection = Arrays.asList(new Category[] { _0, existing, _2, _3, _4 });
		when(dao.select(anyMap(), any(Range.class), anyList())).thenReturn(selection);

		Key<AbstractBase<Category>> key = mock(Key.class);
		when(dao.save(any(Category.class))).thenReturn(key);
		when(key.getId()).thenReturn(1L).thenReturn(2L).thenReturn(id);

		Category update = createCandidate(id, 3L);
		when(dao.get(id)).thenReturn(update);
		assertEquals(update, service.update(existing, update));
		verify(dao, times(3)).get(anyLong());
		verify(dao, times(0)).save(_0);
		verify(dao, times(1)).save(_2);
		verify(dao, times(1)).save(_3);
		verify(dao, times(0)).save(_4);
		verify(dao, times(1)).save(existing);
	}
	
	
	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateOrderMoveBackToFront() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);

		Long id = 12345L;
		Category _0 = createCandidate(0L, 0L);
		Category _1 = createCandidate(1L, 1L);
		Category existing = createCandidate(id, 2L);
		List<Category> selection = Arrays.asList(new Category[] { _0, _1, existing });
		when(dao.select(anyMap(), any(Range.class), anyList())).thenReturn(selection);

		Key<AbstractBase<Category>> key = mock(Key.class);
		when(dao.save(any(Category.class))).thenReturn(key);
		when(key.getId()).thenReturn(0L).thenReturn(1L).thenReturn(id);

		Category update = createCandidate(id, 0L);
		when(dao.get(id)).thenReturn(update);
		assertEquals(update, service.update(existing, update));
		verify(dao, times(3)).get(anyLong());
		verify(dao, times(1)).save(_0);
		verify(dao, times(1)).save(_1);
		verify(dao, times(1)).save(existing);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateOrderGetBack() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);

		Long id = 12345L;
		Category _null = createCandidate(-1L, null);
		Category _0 = createCandidate(0L, 0L);
		Category _1 = createCandidate(1L, 1L);
		Category _2 = createCandidate(2L, 2L);
		Category existing = createCandidate(id, 3L);
		Category _4 = createCandidate(4L, 4L);
		List<Category> selection = Arrays.asList(new Category[] { _null, _0, _1, _2, existing, _4 });
		when(dao.select(anyMap(), any(Range.class), anyList())).thenReturn(selection);

		Key<AbstractBase<Category>> key = mock(Key.class);
		when(dao.save(any(Category.class))).thenReturn(key);
		when(key.getId()).thenReturn(1L).thenReturn(2L).thenReturn(id);

		Category update = createCandidate(id, 1L);
		when(dao.get(id)).thenReturn(update);
		assertEquals(update, service.update(existing, update));
		verify(dao, times(3)).get(anyLong());
		verify(dao, times(0)).save(_0);
		verify(dao, times(1)).save(_1);
		verify(dao, times(1)).save(_2);
		verify(dao, times(0)).save(_4);
		verify(dao, times(1)).save(existing);
	}

	@Test(expected = ServerErrorException.class)
	@SuppressWarnings("unchecked")
	public void testUpdatewithNonCloneable() {
		CategoryDao dao = mock(CategoryDao.class);
		UserService userService = mock(UserService.class);
		CategoryService service = new CategoryService(dao, userService);
		
		when(userService.isLoggedAdmin()).thenReturn(Boolean.TRUE);
		
		Long id = 12345L;
		Category existing = createCandidate(id, 1L);
		Category update = createCandidate(id, 0L);
		Category nonCloneable = new Category() {
			@Override
			public Category clone() throws CloneNotSupportedException {
				throw new CloneNotSupportedException("Done in purpose");
			}
		};
		nonCloneable.setId(1L);
		nonCloneable.setOrder(0L);
		List<Category> selection = Arrays.asList(new Category[] { nonCloneable, existing });
		when(dao.select(anyMap(), any(Range.class), anyList())).thenReturn(selection);

		Key<AbstractBase<Category>> key = mock(Key.class);
		when(dao.save(existing)).thenReturn(key);
		
		service.update(existing, update);
	}
}
