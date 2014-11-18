package com.lepin.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 记录列表页数，总页数，以及对象集合
 * 
 * @param <T>
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private int total;// 总条数
	private int pageNumber;// 当前页码
	private int pageSize;// 每页数量
	private List<T> rows;// 数据

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	/**
	 * 获取总页数
	 * 
	 * @return
	 */
	public int getPageCount() {
		return (int) Math.ceil(total * 1.0 / pageSize);
	}

	@Override
	public String toString() {
		return "Page [total=" + total + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize
				+ ", rows=" + rows + "]";
	}

}
