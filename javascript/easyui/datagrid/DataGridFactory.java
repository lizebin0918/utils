package com.talent.common.easyui.datagrid;

import javax.servlet.ServletRequest;

public class DataGridFactory {

	private static DataGridFactory instance = new DataGridFactory();

	private DataGridFactory() {
	}

	public static DataGridFactory getInstance() {
		if (instance == null) {
			instance = new DataGridFactory();
			return instance;
		}
		else
			return instance;
	}
	
	public DataGridModel getDataGridModel(ServletRequest request) {
		return new DataGridModel(request);
	}

}
