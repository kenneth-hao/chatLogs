package com.yappam.openfire.plugin.chatlogs.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Page {

	public static final String ASC = "asc";
	public static final String DESC = "desc";

	private int pageSize = 15;
	private long totalCount;
	private int pageNum = 1;
	private int totalPages;
	private List<?> dateList = new ArrayList<Object>();
	private boolean autoCount = true;
	private boolean more = false;

	protected String orderBy = "createDate";
	protected String order = DESC;

	public Page() {

	}
	
	public boolean isMore() {
		more = totalCount > pageNum * pageSize;
		return more;
	}

	public Page(boolean autoCount) {
		this.autoCount = autoCount;
	}

	public Page(int pageNum) {
		this.pageNum = pageNum;
	}

	public Page(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order));
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		if (totalCount <= 0) {
			this.totalCount = 0;
		} else {
			this.totalCount = totalCount;

			if (this.totalCount % pageSize == 0) {
				setPageCount(Long.valueOf(this.totalCount / pageSize).intValue());
			} else {
				setPageCount(Long.valueOf(this.totalCount / pageSize + 1).intValue());
			}
		}
	}

	public int getPageNum() {
		if (pageNum > totalPages) {
			return totalPages;
		}
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
		if (pageNum < 1) {
			this.pageNum = 1;
		}
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setPageCount(int totalPages) {
		if (totalPages < 0) {
			this.totalPages = 0;
		} else {
			this.totalPages = totalPages;
		}
	}

	public int getBeginIndex() {
		if (pageNum <= 1 || totalPages <= 1) {
			return 0;
		} else {
			return (pageNum - 1) * pageSize;
		}
	}

	public int getEndIndex() {
		return (getBeginIndex() + pageSize);
	}

	public void setDateList(List<?> dateList) {
		this.dateList = dateList;
	}

	public List<?> getDateList() {
		return dateList;
	}

	public boolean isAutoCount() {
		return autoCount;
	}

	public void setAutoCount(boolean autoCount) {
		this.autoCount = autoCount;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrder() {
		return order;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return orderBy;
	}

	@Override
	public String toString() {
		return "Page [autoCount=" + autoCount + ", dateList=" + dateList.size() + ", order=" + order + ", orderBy=" + orderBy + ", pageNum="
				+ pageNum + ", pageSize=" + pageSize + ", totalCount=" + totalCount + ", totalPages=" + totalPages + "]";
	}

}
