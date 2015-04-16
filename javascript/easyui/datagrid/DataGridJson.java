package com.talent.common.easyui.datagrid;

import java.util.Collection;

public class DataGridJson<T> {

	private long total;// 总记录数
	private Collection<T> rows;// 每行记录
	private Collection<T> footer;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Collection<T> getRows() {
		return rows;
	}

	public void setRows(Collection<T> rows) {
		this.rows = rows;
	}

	public Collection<T> getFooter() {
		return footer;
	}

	public void setFooter(Collection<T> footer) {
		this.footer = footer;
	}

}
