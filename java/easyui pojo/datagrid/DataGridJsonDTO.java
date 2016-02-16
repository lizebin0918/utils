package com.lzb.common.easyui.datagrid;

import java.util.ArrayList;
import java.util.Collection;

public class DataGridJsonDTO {

	private long total;// 总记录数
	private Collection rows = new ArrayList();// 每行记录
	private Collection footer = new ArrayList();

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Collection getRows() {
		return rows;
	}

	public void setRows(Collection rows) {
		this.rows = rows;
	}

	public Collection getFooter() {
		return footer;
	}

	public void setFooter(Collection footer) {
		this.footer = footer;
	}

}
