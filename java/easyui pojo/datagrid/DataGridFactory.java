package com.lzb.common.easyui.datagrid;

import java.util.LinkedHashMap;

import javax.servlet.ServletRequest;

public class DataGridFactory {

	private DataGridFactory() {}

	public static DataGridFactory getInstance() {
		return InnerSingletonClass.instance;
	}
	
	private static class InnerSingletonClass {
		private static DataGridFactory instance = new DataGridFactory();
	}
	
	public DataGridDTO getDataGridDTO(ServletRequest request) {
		return new DataGridDTO(request);
	}
	
	public DataGridDTO getDataGridDTO(int page, int rows, LinkedHashMap<String, String> orders) {
		return new DataGridDTO(page, rows, orders);
	}
	
	public DataGridDTO getDataGridDTO() {
		return new DataGridDTO(null);
	}

}