package dderrien.common.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.googlecode.objectify.Key;

import dderrien.common.dao.AbstractDao;
import dderrien.common.exception.ConflictException;
import dderrien.common.exception.NoContentException;
import dderrien.common.exception.NotFoundException;
import dderrien.common.exception.NotModifiedException;
import dderrien.common.model.AbstractBase;
import dderrien.common.util.Range;

public class AbstractServiceTest {

	public class TestModel extends AbstractBase<TestModel> {
		String test;
		public String getTest() { return test; }
		public void setTest(String test) { this.test = test; }
	};
	public class TestDao extends AbstractDao<TestModel> {};

	@Test
	public void testConstructor() {
		new AbstractService<TestModel>(new TestDao()) {};
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectSilent() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		List<TestModel> selection = new ArrayList<TestModel>();
		when(dao.select(null, null, null)).thenReturn(selection);
		
		assertEquals(selection, service.selectSilent(null, null, null));
		verify(dao, times(1)).select(null, null, null);
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}

	@Test(expected = NoContentException.class)
	public void testSelectWithoutContent() {
		TestDao dao = mock(TestDao.class);
		when(dao.getModelClass()).thenReturn(TestModel.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		when(dao.select(null, null, null)).thenReturn(selection);
		
		service.select(null, null, null);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithContent() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		List<TestModel> selection = Arrays.asList(new TestModel[] { new TestModel() });
		when(dao.select(null, null, null)).thenReturn(selection);
		
		assertEquals(selection, service.select(null, null, null));
		verify(dao, times(1)).select(null, null, null);
		verify(dao, times(1)).select(anyMap(), any(Range.class), anyList());
	}

	@Test
	public void testGetSilent() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		TestModel candidate = new TestModel();
		when(dao.get(id)).thenReturn(candidate);
		
		assertEquals(candidate, service.getSilent(id));
		verify(dao, times(1)).get(id);
		verify(dao, times(1)).get(anyLong());
	}

	@Test(expected = NotFoundException.class)
	public void testGetWithoutContent() {
		TestDao dao = mock(TestDao.class);
		when(dao.getModelClass()).thenReturn(TestModel.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		when(dao.get(id)).thenReturn(null);
		
		service.get(id);
	}

	@Test
	public void testGetWithContent() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		TestModel candidate = new TestModel();
		when(dao.get(id)).thenReturn(candidate);
		
		assertEquals(candidate, service.get(id));
		verify(dao, times(1)).get(id);
		verify(dao, times(1)).get(anyLong());
	}

	@Test(expected = ConflictException.class)
	public void testCreateWithConflict() {
		TestDao dao = mock(TestDao.class);
		when(dao.getModelClass()).thenReturn(TestModel.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		TestModel candidate = new TestModel();
		candidate.setId(id);
		when(dao.get(id)).thenReturn(candidate);
		
		service.create(candidate);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateWithoutConflictI() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		TestModel candidate = new TestModel();
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.save(candidate)).thenReturn(key);
		
		assertEquals(key, service.create(candidate));
		verify(dao, times(0)).get(anyLong());
		verify(dao, times(1)).save(candidate);
		verify(dao, times(1)).save(any(TestModel.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateWithoutConflictII() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		TestModel candidate = new TestModel();
		candidate.setId(id);
		when(dao.get(id)).thenReturn(null);
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.save(candidate)).thenReturn(key);
		
		assertEquals(key, service.create(candidate));
		verify(dao, times(1)).get(id);
		verify(dao, times(1)).get(anyLong());
		verify(dao, times(1)).save(candidate);
		verify(dao, times(1)).save(any(TestModel.class));
	}

	@Test(expected = NotModifiedException.class)
	public void testUpdateWithoutModification() {
		TestDao dao = mock(TestDao.class);
		when(dao.getModelClass()).thenReturn(TestModel.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		TestModel original = new TestModel();
		original.setId(id);
		TestModel candidate = new TestModel();
		candidate.setId(id);
		when(dao.get(id)).thenReturn(candidate);
		
		service.update(id, candidate);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUpdateWithModification() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		TestModel original = new TestModel();
		original.setId(id);
		original.setTest("original");
		TestModel candidate = new TestModel();
		candidate.setId(id);
		candidate.setTest("candidate");
		Key<AbstractBase<TestModel>> key = mock(Key.class);
		when(dao.save(original)).thenReturn(key);
		when(key.getId()).thenReturn(id);
		when(dao.get(id)).thenReturn(candidate);
		
		assertEquals(candidate, service.update(original, candidate));
		verify(dao, times(1)).save(original);
		verify(dao, times(1)).save(any(TestModel.class));
		verify(key, times(1)).getId();
		verify(dao, times(1)).get(id);
		verify(dao, times(1)).get(anyLong());
	}
	
	@Test
	public void testDelete() {
		TestDao dao = mock(TestDao.class);
		AbstractService<TestModel> service = new AbstractService<TestModel>(dao) {};
		
		Long id = 12345L;
		when(dao.get(id)).thenReturn(new TestModel());
		
		service.delete(id);
		verify(dao, times(1)).get(id);
		verify(dao, times(1)).get(anyLong());
		verify(dao, times(1)).delete(id);
		verify(dao, times(1)).delete(anyLong());
	}
}
