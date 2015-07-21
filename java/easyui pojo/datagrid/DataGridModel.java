package com.talent.common.easyui.datagrid;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

/**
 *EasyuiDataGridDto是获得action中的请求，把page(pageNo)和rows(pageSize)拿到
 * 
 * 接受参数：page , rows
 * 
 */
public class DataGridModel {

	private int page = 1;// 当前页
	private int rows = 10;// 每页显示记录数
	private Map<String, String> order = new HashMap<String, String>();// 排序字段名
	
	public DataGridModel(ServletRequest request) {
		this.rows = request.getParameter("rows")==null ? rows :Integer.parseInt(request.getParameter("rows"));
		this.page = request.getParameter("page") == null ?page :Integer.parseInt(request.getParameter("page"));
		String key = null;
		key = request.getParameter("sort");
		//这里默认是id升序。
		if(key == null) {
			key = "id";
		}
		String value = request.getParameter("order");
		if(value == null) {
			value = "desc";
		}
		order.put(key, value);
	} 
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	public Map<String, String> getOrder() {
		return order;
	}
	public void setOrder(Map<String, String> order) {
		this.order = order;
	}
}
