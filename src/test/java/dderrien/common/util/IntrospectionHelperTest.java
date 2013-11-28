package dderrien.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class IntrospectionHelperTest {

	public class TestModel {}
	public class TestFake {}
	
	public class AbstractDao<T> {}
	public class ConcreteDao extends AbstractDao<TestModel> {}

	public class AbstractAbstractMetaDao<T> {}
	public class AbstractMetaDao extends AbstractAbstractMetaDao<TestModel>{}
	public class ConcreteMetaDao extends AbstractMetaDao {}

	public class AbstractService<T, V> {}
	public class ConcreteService extends AbstractService<TestFake, TestModel> {}

	@Test
	public void testConstructor() {
		new IntrospectionHelper();
	}
	
	@Test
	public void testGetFirstTypeArgumentTestModel() {
		assertNull(IntrospectionHelper.getFirstTypeArgument(TestModel.class));
	}

	@Test
	public void testGetFirstTypeArgumentConcreteDao() {
		assertEquals(TestModel.class, IntrospectionHelper.getFirstTypeArgument(ConcreteDao.class));
	}

	@Test
	public void testGetFirstTypeArgumentConcreteMetaDao() {
		assertEquals(TestModel.class, IntrospectionHelper.getFirstTypeArgument(ConcreteMetaDao.class));
	}

	@Test
	public void testGetTypeArgumentConcreteDao() {
		assertNull(IntrospectionHelper.getTypeArgument(ConcreteDao.class, 1));
	}
}
