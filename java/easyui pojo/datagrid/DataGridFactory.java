package com.talent.common.easyui.datagrid;

import javax.servlet.ServletRequest;

public class DataGridFactory {

	private static volatile DataGridFactory instance = null;

	private DataGridFactory() {
	}

	public static DataGridFactory getInstance() {
		if (instance == null) {
    	    synchronized(DataGridFactory.class){
		        if(null == instance){
		            instance = new DataGridFactory();
		        }
		    }
		}
		return instance;
	}
	
	public DataGridModel getDataGridModel(ServletRequest request) {
		return new DataGridModel(request);
	}

}