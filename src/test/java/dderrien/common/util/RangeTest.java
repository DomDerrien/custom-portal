package dderrien.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import dderrien.common.exception.InvalidRangeException;

import javax.ws.rs.core.Response.Status;

public class RangeTest {

	@Test
	public void testAccessors() {
		Integer startRow = Integer.valueOf(12345);
		Integer endRow = Integer.valueOf(54321);
		Integer count = Integer.valueOf(67890);
		Integer total = Integer.valueOf(1234567890);
		Integer listSize = Integer.valueOf(1234567890);

		Range range = new Range();
		range.setStartRow(startRow);
		range.setEndRow(endRow);
		range.setCount(count);
		range.setTotal(total);
		range.setListSize(listSize);

		assertEquals(startRow, range.getStartRow());
		assertEquals(endRow, range.getEndRow());
		assertEquals(count, range.getCount());
		assertEquals(total, range.getTotal());
		assertEquals(listSize, range.getListSize());
		
		assertFalse(range.isInitialized()); // Not yet parsed
	}

	@Test
	public void testNoHeader() {
		Range range = Range.parse((String) null);
		assertNotNull(range);
		assertEquals(0, range.getStartRow().intValue());
		assertNull(range.getEndRow());
		assertNull(range.getCount());
	}

	@Test
	public void testContentEmpty() {
		Range range = Range.parse((String) "items=");
		assertNotNull(range);
		assertEquals(0, range.getStartRow().intValue());
		assertNull(range.getEndRow());
		assertNull(range.getCount());
	}

	@Test(expected = InvalidRangeException.class)
	public void testNoItemsKeyWordHeader() {
		Range.parse("2-5");
	}

	@Test(expected = InvalidRangeException.class)
	public void testInValidToMuchDataInHeader() {
		Range.parse("items=2-5-7");
	}

	@Test
	public void testValidWithEndRow() {
		Range range = Range.parse("items=2-5");
		assertNotNull(range);
		assertEquals(range.getStartRow().intValue(), 2);
		assertEquals(range.getEndRow().intValue(), 5);
		assertEquals(range.getCount().intValue(), 4);
	}

	@Test
	public void testValidWithSpaceInsteadOfEndRow() {
		Range range = Range.parse("items=2- ");
		assertNotNull(range);
		assertEquals(range.getStartRow().intValue(), 2);
		assertEquals(range.getEndRow(), null);
		assertEquals(range.getCount(), null);
	}

	@Test
	public void testValidWithNoEndRow() {
		Range range = Range.parse("items=2-");
		assertNotNull(range);
		assertEquals(range.getStartRow().intValue(), 2);
		assertEquals(range.getEndRow(), null);
		assertEquals(range.getCount(), null);
	}

	@Test(expected = InvalidRangeException.class)
	public void testValidWithEndRowLessThanStartRow() {
		Range.parse("items=5-2");
	}

	@Test(expected = InvalidRangeException.class)
	public void testNumberFormatExceptionI() {
		Range.parse("items=aaaa-2");
	}

	@Test(expected = InvalidRangeException.class)
	public void testNumberFormatExceptionII() {
		Range.parse("items=2-bbbbb");
	}

	@Test
	public void testValidToStringWithEndRow() {
		Range range = Range.parse("items=2-5");
		range.setTotal(10);
		String toString = range.toContentRangeHeader();
		assertEquals(toString, "items=2-5/10");
	}

	@Test
	public void testValidToStringWithZeroStartRow() {
		Range range = Range.parse("items=0-2");
		range.setTotal(10);
		String toString = range.toContentRangeHeader();
		assertEquals(toString, "items=0-2/10");
	}

	@Test
	public void testValidToStringWithNoEndRow() {
		Range range = Range.parse("items=2");
		range.setTotal(10);
		String toString = range.toContentRangeHeader();
		assertEquals(toString, "items=2-9/10");
	}

	@Test
	public void testValidToStringWithEndRowGreaterSize() {
		Range range = Range.parse("items=2-200");
		range.setTotal(10);
		String toString = range.toContentRangeHeader();
		assertEquals(toString, "items=2-9/10");
	}

	@Test(expected = InvalidRangeException.class)
	public void testInvalidToString() {
		Range range = Range.parse("items=10-15");
		range.setTotal(10);
		range.toContentRangeHeader();
	}

	@Test
	public void testNoContent() {
		Range range = Range.parse("items=10-15");
		range.setCount(20);
		range.setListSize(0);
		assertEquals(Status.NO_CONTENT.getStatusCode(), range.getResponseStatus());
	}

	@Test
	public void testResponseNoContent() {
		Range range = new Range();
		range.setListSize(0);
		assertEquals(Status.NO_CONTENT.getStatusCode(), range.getResponseStatus());
	}

	@Test
	public void testResponsePartial() {
		Range range = new Range();
		range.setTotal(30);
		range.setListSize(6);
		assertEquals(Status.PARTIAL_CONTENT.getStatusCode(), range.getResponseStatus());
	}

	@Test
	public void testResponseOK() {
		Range range = new Range();
		range.setTotal(30);
		range.setListSize(30);
		assertEquals(Status.OK.getStatusCode(), range.getResponseStatus());

	}
}
