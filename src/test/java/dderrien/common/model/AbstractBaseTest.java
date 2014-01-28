package dderrien.common.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.junit.Test;

import dderrien.common.exception.ServerErrorException;
import dderrien.common.model.AbstractBase;

public class AbstractBaseTest {

	public class TestModel extends AbstractBase<TestModel> {};
	public class ExtendedTestModel extends TestModel {
		Boolean boolT = Boolean.TRUE;
		Boolean boolF = Boolean.FALSE;
		String str = "String";
		Double dbl = 2.45D;
		BigInteger bInt = BigInteger.TEN;
		public Boolean getBoolT() { return boolT; }
		public Boolean getBoolF() { return boolF; }
		public String getStr() { return str; }
		public Double getDbl() { return dbl; }
		public BigInteger getBInt() { return bInt; }
		public void setBoolT(Boolean bool) { this.boolT = bool; }
		public void setBoolF(Boolean bool) { this.boolF = bool; }
		public void setStr(String str) { this.str = str; }
		public void setDbl(Double dbl) { this.dbl = dbl; }
		public void setBInt(BigInteger bInt) { this.bInt = bInt; }
	};
	public class CorruptedTestModel extends TestModel {
		String str = "String";
		public String getStr() { throw new NullPointerException("Done in purpose"); }
	};
	
	@Test
	public void testConstructor() {
		new TestModel();
	}
	
	@Test
	public void testAccessors() {
		TestModel entity = new TestModel();
		
		Long id = 12345L;
		Date creation = new Date(123L);
		Long version = 654756232343L;
		
		entity.setId(id);
		entity.setCreation(creation);
		entity.setVersion(version);
		
		assertEquals(id, entity.getId());
		assertEquals(creation, entity.getCreation());
		assertEquals(version, entity.getVersion());
	}
	
	@Test
	public void testToStringEmtpy() {
		TestModel entity = new TestModel();
		
		assertEquals("{  }", entity.toString());
	}
	
	@Test
	public void testToStringBase() {
		TestModel entity = new TestModel();
		entity.setId(12345L);
		entity.setCreation(new Date(123L));
		entity.setVersion(456789L);
		
		assertEquals("{ \"id\": 12345, \"creation\": 123, \"version\": 456789 }", entity.toString());
	}
	
	@Test
	public void testToStringExtended() {
		TestModel entity = new ExtendedTestModel();
		
		assertEquals("{ \"boolT\": true, \"str\": \"String\", \"boolF\": false, \"BInt\": \"10\", \"dbl\": 2.45 }", entity.toString());
	}
	
	@Test
	public void testToStringCorrupted() {
		TestModel entity = new CorruptedTestModel();
		
		assertEquals("{ \"ex\": \"InvocationTargetException in " + CorruptedTestModel.class.getSimpleName() + ".toString()\" }", entity.toString());
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
		ExtendedTestModel entity = new ExtendedTestModel();
		entity.setId(12345L);
		entity.setCreation(new Date(123L));
		
		ExtendedTestModel clone = (ExtendedTestModel) entity.clone();
		assertNotSame(entity, clone);
		assertEquals(entity.getId(), clone.getId());
		assertEquals(entity.getBInt(), clone.getBInt());
	}
	
	@Test
	public void testMergeNone() {
		Long id = 12345L;
		Date creation = new Date(123L);
		Long version = 56789L;

		TestModel entity = new TestModel();
		entity.setId(id);
		entity.setCreation(creation);
		entity.setVersion(version);
		
		TestModel update = new TestModel();
		update.setId(id * 2);
		update.setCreation(new Date(creation.getTime() * 2));
		update.setVersion(version * 2);
		
		assertFalse(entity.merge(update));
		assertEquals(id, entity.getId());
		assertEquals(creation, entity.getCreation());
		assertEquals(version, entity.getVersion());
	}
	
	@Test
	public void testMergeRevealed() {
		Long id = 12345L;
		Date creation = new Date(123L);
		String str = "String";

		ExtendedTestModel entity = new ExtendedTestModel();
		entity.setId(id);
		entity.setCreation(null);
		entity.setStr(str);
		entity.setBoolT(Boolean.TRUE);
		entity.setBoolF(Boolean.FALSE);
		
		ExtendedTestModel update = new ExtendedTestModel();
		update.setId(id * 2);
		update.setCreation(new Date(creation.getTime() * 2));
		update.setStr(str + str);
		update.setBoolT(Boolean.TRUE);
		update.setBoolF(null);
		
		assertTrue(entity.merge(update));
		assertEquals(id, entity.getId());
		assertNull(entity.getCreation());
		assertEquals(str + str, entity.getStr());
		assertTrue(entity.getBoolT());
		assertFalse(entity.getBoolF());
	}
	
	@SuppressWarnings("unused")
	public class IllegalReaderTestModel extends TestModel {
		private Integer nb = Integer.MAX_VALUE;
		public Integer getNb() { throw new NullPointerException("Done in purpose"); }
		public void setNb(Integer nb) { this.nb = nb; }
	}

	@Test(expected = ServerErrorException.class)
	public void testMergeWithInvocationTargetException() {
		new IllegalReaderTestModel().merge(new IllegalReaderTestModel());
	}

	@Test
	public void testIsSmeValue() {
		assertTrue(AbstractBase.isSameValue(null, null));
		assertTrue(AbstractBase.isSameValue("string", "string"));
		assertTrue(AbstractBase.isSameValue(1L, 1L));
		assertTrue(AbstractBase.isSameValue(.11d, .11d));
		assertTrue(AbstractBase.isSameValue(new BigDecimal(1L), new BigDecimal(1L)));

		assertFalse(AbstractBase.isSameValue(null, "string"));
		assertFalse(AbstractBase.isSameValue("string", null));
		assertFalse(AbstractBase.isSameValue(new BigDecimal(.33333333d), new BigDecimal(1/3)));
		assertFalse(AbstractBase.isSameValue(new BigDecimal(1/3), new BigDecimal(.33333333d)));
	}

	@Test
	public void testPrePersit() {
		TestModel entity = new TestModel();
		Date creation1 = entity.getCreation();
		assertNull(creation1);
		Long version1 = entity.getVersion();
		assertNull(version1);
		
		entity.setCreationAndVersion();
		Date creation2 = entity.getCreation();
		assertNotNull(creation2);
		Long version2 = entity.getVersion();
		assertNotNull(version2);

		entity.setCreationAndVersion();
		Date creation3 = entity.getCreation();
		assertNotNull(creation3);
		assertEquals(creation2, creation3);
		Long version3 = entity.getVersion();
		assertEquals(version2.longValue() + 1L, version3.longValue());
	}
}
