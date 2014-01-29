package dderrien.common.util;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import dderrien.common.exception.InvalidRangeException;

/**
 * Holder of relevant data for scrolling that parses Range header from HttpServletRequest.
 * 
 * Degraded formats from the standard startRow-endRow are accepted such as startRow- and startRow.
 * {@link InvalidRangeException} may be raised should boundaries not be parsable.
 * 
 */
public class Range {
    public final static String HEADER_NAME = "Range";

    private final static String ITEMS = "items=";

    /**
     * true if header present and successfully parsed
     */
    private boolean initialized = false;

    /**
     * Passed on to service and DAO
     */
    private Integer startRow = 0;

    /**
     * Passed on to service and DAO if not null
     */
    private Integer count = null;

    /**
     * Passed on to service and DAO but not used for querying, count is used instead, used to potentially raise an
     * InvalidRangeException within the DAO should the total not fit the requested boundaries
     */
    private Integer endRow;

    /**
     * Output value to inform the UI on the total instances available for the set perimeter
     */
    private Integer total = 0; // for resources mock purposes

    /**
     * output list size to determine response {@link HttpStatus}
     */
    private Integer listSize = 0; // for resources mock purposes

    public static Range parse(String rangeHeader) {
        Range range = new Range();

        // spring considers a header to be null if only made of spaces
        if (rangeHeader == null) {
            return range;
        }

        if (!rangeHeader.trim().startsWith(ITEMS)) {
            throw new InvalidRangeException("Range header mal formed for scrolling");
        }

        rangeHeader = rangeHeader.trim().replace(ITEMS, "");

        String[] keys = rangeHeader.split("-");

        if (keys.length > 2) { // could be 1
            throw new InvalidRangeException("Range header mal formed for scrolling");
        }

        try {
            String startRowStr = keys[0].trim();

            if (!StringUtils.isEmpty(startRowStr)) {
                range.startRow = Integer.valueOf(startRowStr);
            }

            // at this stage startRow < 0 cannot arise : minus sign and dash are
            // the same !

            if (keys.length == 2) {
                String endRowStr = keys[1].trim();

                // at this stage endRowStr cannot be empty

                range.endRow = Integer.valueOf(endRowStr);
                range.count = range.endRow - range.startRow + 1;

                if (range.endRow < range.startRow) {
                    throw new InvalidRangeException("Range header mal formed for scrolling");
                }
            }
        }
        catch (NumberFormatException nfe) {
            throw new InvalidRangeException("Range header mal formed for scrolling");
        }

        range.initialized = true;
        return range;
    }

    /**
     * Method to form paging response header. may raise a {@link InvalidRangeException} in case of inconsistent
     * boundaries. <br/>
     * Note : same control is to be applied at DAO level
     */
    public String toContentRangeHeader() {
        if (this.startRow > 0 && this.startRow >= this.total) { // double check with control within DAO
            throw new InvalidRangeException("Range header mal formed for scrolling");
        }

        if (this.endRow == null || this.total <= this.endRow) {
            return ITEMS + this.startRow + "-" + (this.total - 1) + "/" + this.total;
        }

        return ITEMS + this.startRow + "-" + this.endRow + "/" + this.total;
    }

    public int getResponseStatus() {
        if (listSize == 0) {
            return Status.NO_CONTENT.getStatusCode();
        }

        if (listSize < this.total) {
            return Status.PARTIAL_CONTENT.getStatusCode();
        }

        return Status.OK.getStatusCode();
    }

    // Accessors

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getEndRow() {
        return endRow;
    }

    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getListSize() {
        return listSize;
    }

    public void setListSize(Integer listSize) {
        this.listSize = listSize;
    }

    public boolean isInitialized() {
        return initialized;
    }
}