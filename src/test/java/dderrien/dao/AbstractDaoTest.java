package dderrien.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.DeleteType;
import com.googlecode.objectify.cmd.Deleter;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;
import com.googlecode.objectify.cmd.Saver;

import dderrien.exception.ClientErrorException;
import dderrien.exception.InvalidRangeException;
import dderrien.model.AbstractBase;
import dderrien.util.Range;

public class AbstractDaoTest {

	class TestModel extends AbstractBase<TestModel> {};
	
	@Test
	public void testConstructor() {
		new AbstractDao<TestModel>() {};
	}
	
	@Test
	public void testGetModelClass() {
		AbstractDao<TestModel> dao = new AbstractDao<TestModel>() {};
		assertEquals(TestModel.class, dao.getModelClass());
	}
	
	@Test
	public void testGetOfy() {
		AbstractDao<TestModel> dao = new AbstractDao<TestModel>() {};
		assertNotNull(dao.getOfy());
	}
	
	@Test
	public void testGetQuery() {
		AbstractDao<TestModel> dao = new AbstractDao<TestModel>() {};
		assertNotNull(dao.getQuery(TestModel.class));
	}
	
	@Test(expected = ClientErrorException.class)
	public void testGetByKeyNull() {
		AbstractDao<TestModel> dao = new AbstractDao<TestModel>() {};
		dao.get(null);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetByKey() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		Long realKey = Long.valueOf(12345L);
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		LoadResult<TestModel> resultLoader = mock(LoadResult.class);
		TestModel realValue = new TestModel();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.id(realKey)).thenReturn(resultLoader);
		when(resultLoader.now()).thenReturn(realValue);
		
		assertEquals(realValue, dao.get(realKey));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).id(realKey);
		verify(resultLoader, times(1)).now();
	}

	@Test(expected = ClientErrorException.class)
	public void testFilterWithNullCondition() {
		AbstractDao<TestModel> dao = new AbstractDao<TestModel>() {};
		dao.filter(null, null);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testFilter() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		Long realKey = Long.valueOf(12345L);
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		LoadResult<TestModel> resultLoader = mock(LoadResult.class);
		TestModel realValue = new TestModel();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.filter(condition, realKey)).thenReturn(typedLoader);
		when(typedLoader.first()).thenReturn(resultLoader);
		when(resultLoader.now()).thenReturn(realValue);
		
		assertEquals(realValue, dao.filter(condition, realKey));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).filter(condition, realKey);
		verify(typedLoader, times(1)).first();
		verify(resultLoader, times(1)).now();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAll() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(null, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(0)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionAndValueNull() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, null);
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(0)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionAndValueEmtpyString() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, "");
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(0)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionAndValueAny() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, "*");
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(0)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionEqualsToLong() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		String updatedCondition = condition + " = ";
		Long realKey = Long.valueOf(12345L);
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, realKey);
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.filter(updatedCondition, realKey)).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(1)).filter(updatedCondition, realKey);
		verify(typedLoader, times(1)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionGreaterToLong() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key <";
		Long realKey = Long.valueOf(12345L);
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, realKey);
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.filter(condition, realKey)).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(1)).filter(condition, realKey);
		verify(typedLoader, times(1)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionEqualsToString() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "name";
		String updatedCondition = condition + " = ";
		String realName = "Joe";
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, realName);
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.filter(updatedCondition, realName)).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(1)).filter(updatedCondition, realName);
		verify(typedLoader, times(1)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionStartsWithString() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "name";
		String realName = "Joe";
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, realName + "*");
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.filter(condition + " >= ", realName)).thenReturn(typedLoader);
		when(typedLoader.filter(condition + " < ", realName + "\uFFFD")).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(1)).filter(condition + " >= ", realName);
		verify(typedLoader, times(1)).filter(condition + " < ", realName + "\uFFFD");
		verify(typedLoader, times(2)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionEqualsToListEmpty() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		List<Long> realKeys = new ArrayList<Long>();
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, realKeys);
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(0)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionEqualsToListOfOneItem() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		String updatedCondition = condition + " = ";
		List<Long> realKeys = Arrays.asList(new Long[] { Long.valueOf(12345L) });
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, realKeys);
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.filter(updatedCondition, realKeys.get(0))).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(1)).filter(updatedCondition, realKeys.get(0));
		verify(typedLoader, times(1)).filter(anyString(), anyObject());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectWithConditionGreaterToListOfManyItems() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		String condition = "key";
		String updatedCondition = condition + " IN ";
		List<Long> realKeys = Arrays.asList(new Long[] { Long.valueOf(12345L), Long.valueOf(345678L) });
		HashMap<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(condition, realKeys);
		
		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.filter(updatedCondition, realKeys)).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(criteria, null, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(1)).filter(updatedCondition, realKeys);
		verify(typedLoader, times(1)).filter(anyString(), anyObject());
	}
	
	@Test(expected = InvalidRangeException.class)
	@SuppressWarnings("unchecked")
	public void testApplyRangeInvalid() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		LoadType<TestModel> typedLoader = mock(LoadType.class);
		QueryKeys<TestModel> keyLoader = mock(QueryKeys.class);
		List<Key<TestModel>> keys = mock(List.class);
		Range range = mock(Range.class);

		when(range.getStartRow()).thenReturn(2);
		when(typedLoader.keys()).thenReturn(keyLoader);
		when(keyLoader.list()).thenReturn(keys);
		when(keys.size()).thenReturn(1);

		dao.applyRange(typedLoader, range);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testApplyRange() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		LoadType<TestModel> typedLoader = mock(LoadType.class);
		QueryKeys<TestModel> keyLoader = mock(QueryKeys.class);
		List<Key<TestModel>> keys = mock(List.class);
		Range range = mock(Range.class);

		when(range.getStartRow()).thenReturn(2);
		when(range.getCount()).thenReturn(4);
		when(typedLoader.keys()).thenReturn(keyLoader);
		when(keyLoader.list()).thenReturn(keys);
		when(keys.size()).thenReturn(4);
		when(typedLoader.offset(2)).thenReturn(typedLoader);
		when(typedLoader.limit(4)).thenReturn(typedLoader);

		assertEquals(typedLoader, dao.applyRange(typedLoader, range));
		verify(range, times(1)).getStartRow();
		verify(range, times(1)).getCount();
		verify(range, times(1)).setTotal(4);
		verify(range, times(1)).setTotal(anyInt());
		verify(typedLoader, times(1)).offset(2);
		verify(typedLoader, times(1)).offset(anyInt());
		verify(typedLoader, times(1)).limit(4);
		verify(typedLoader, times(1)).limit(anyInt());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAllWithRange() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		LoadType<TestModel> typedLoader = mock(LoadType.class);
		QueryKeys<TestModel> keyLoader = mock(QueryKeys.class);
		List<Key<TestModel>> keys = mock(List.class);
		Range range = mock(Range.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		doReturn(true).when(range).isInitialized();
		when(range.getStartRow()).thenReturn(0);
		when(range.getCount()).thenReturn(null);
		when(typedLoader.keys()).thenReturn(keyLoader);
		when(keyLoader.list()).thenReturn(keys);
		when(keys.size()).thenReturn(4);
		when(typedLoader.offset(0)).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);

		assertEquals(selection, dao.select(null, range, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(dao, times(1)).applyRange(typedLoader, range);
		verify(typedLoader, times(1)).list();
		verify(range, times(2)).isInitialized();
		verify(range, times(1)).setListSize(selection.size());
		verify(typedLoader, times(0)).filter(anyString(), anyObject());

		verify(range, times(1)).getStartRow();
		verify(range, times(1)).getCount();
		verify(range, times(1)).setTotal(4);
		verify(range, times(1)).setTotal(anyInt());
		verify(typedLoader, times(1)).offset(0);
		verify(typedLoader, times(1)).offset(anyInt());
		verify(typedLoader, times(0)).limit(anyInt());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAllWithRangeNotInitialized() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		LoadType<TestModel> typedLoader = mock(LoadType.class);
		Range range = mock(Range.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		doReturn(false).when(range).isInitialized();
		when(typedLoader.list()).thenReturn(selection);

		assertEquals(selection, dao.select(null, range, null));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(dao, times(0)).applyRange(typedLoader, range);
		verify(typedLoader, times(1)).list();

		verify(range, times(0)).getStartRow();
		verify(range, times(0)).getCount();
		verify(range, times(0)).setTotal(anyInt());
		verify(typedLoader, times(0)).offset(anyInt());
		verify(typedLoader, times(0)).limit(anyInt());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAllWithEmptyOrder() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(null, null, new ArrayList<String>()));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(0)).filter(anyString(), anyObject());
		verify(typedLoader, times(0)).order(anyString());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSelectAllWithMixedOrder() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		LoadType<TestModel> typedLoader = mock(LoadType.class);
		ArrayList<TestModel> selection = new ArrayList<TestModel>();
		
		doReturn(typedLoader).when(dao).getQuery(TestModel.class);
		when(typedLoader.order("key")).thenReturn(typedLoader);
		when(typedLoader.order("-name")).thenReturn(typedLoader);
		when(typedLoader.list()).thenReturn(selection);
		
		assertEquals(selection, dao.select(null, null, Arrays.asList(new String[] { "+key", "-name" })));
		verify(dao, times(1)).getQuery(TestModel.class);
		verify(typedLoader, times(1)).list();
		verify(typedLoader, times(0)).filter(anyString(), anyObject());
		verify(typedLoader, times(1)).order("key");
		verify(typedLoader, times(1)).order("-name");
		verify(typedLoader, times(2)).order(anyString());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSave() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});
		
		Objectify ofy = mock(Objectify.class);
		Saver saver = mock(Saver.class);
		TestModel candidate = new TestModel();
		Result<Key<TestModel>> savedResult = mock(Result.class);
		Key<TestModel> savedKey = mock(Key.class);
		
		doReturn(ofy).when(dao).getOfy();
		when(ofy.save()).thenReturn(saver);
		when(saver.entity(candidate)).thenReturn(savedResult);
		when(savedResult.now()).thenReturn(savedKey);
		
		assertEquals(savedKey, dao.save(candidate));
		verify(dao, times(1)).getOfy();
		verify(savedResult, times(1)).now();
	}
	
	@Test(expected = ClientErrorException.class)
	public void testDeleteWithKeyNull() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});

		dao.delete(null);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testDeleteWithKey() {
		AbstractDao<TestModel> dao = spy(new AbstractDao<TestModel>() {});
		
		Objectify ofy = mock(Objectify.class);
		Deleter deleter = mock(Deleter.class);
		Long key = Long.valueOf(12345L);
		DeleteType deletedType = mock(DeleteType.class);
		Result<Void> deletedResult = mock(Result.class);

		doReturn(ofy).when(dao).getOfy();
		when(ofy.delete()).thenReturn(deleter);
		when(deleter.type(TestModel.class)).thenReturn(deletedType);
		when(deletedType.id(key)).thenReturn(deletedResult);
		when(deletedResult.now()).thenReturn(null);
		
		dao.delete(key);
		verify(dao, times(1)).getOfy();
		verify(deletedResult, times(1)).now();
	}
}