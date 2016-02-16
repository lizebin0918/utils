package com.lzb.common.easyui.datagrid;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

/**
 *EasyuiDataGridDto是获得action中的请求，把page(pageNo)和rows(pageSize)拿到, orders 排序{字段, "DESC" or "ASC"}
 * 
 * 接受参数：page , rows , orders 
 * 
 */
public class DataGridDTO {

	private int page = DataGridConstValues.PAGE_NO;// 当前页
	private int rows = DataGridConstValues.PAGE_SIZE;// 每页显示记录数
	private int startIndex = (page - 1) * rows;
	private LinkedHashMap<String, String> orders = new LinkedHashMap<String, String>();// 排序字段名
	
	/**
	 * @param page 页码
	 * @param rows 页行数
	 * @param orders 排序{字段, "DESC" or "ASC"}
	 */
	public DataGridDTO(int page, int rows, LinkedHashMap<String, String> orders) {
		this.page = page;
		this.rows = rows;
		this.orders = orders;
		this.startIndex = (page - 1) * rows;
	}
	
	public DataGridDTO(ServletRequest request) {
		if(request == null) {
			return;
		}
		this.rows = request.getParameter("rows")==null ? rows :Integer.parseInt(request.getParameter("rows"));
		this.page = request.getParameter("page") == null ?page :Integer.parseInt(request.getParameter("page"));
		this.startIndex = (page - 1) * rows;
		String[] keys = request.getParameterValues("sort");
		String[] values = request.getParameterValues("order");
		if(keys != null && values != null && keys.length != 0 && values.length != 0) {
			if(keys.length == values.length) {
				for(int i=0; i<keys.length; i++) {
					orders.put(keys[i], values[i]);
				}
			}
		}
	} 
	
	public int getPage() {
		return page;
	}
	public int getRows() {
		return rows;
	}
	public Map<String, String> getOrders() {
		return orders;
	}
	public int getStartIndex() {
		return startIndex;
	}
}
