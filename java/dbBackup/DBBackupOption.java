package com.yjt.gcss.timerTask.dbBackup;

import java.util.List;

/**
 * ���ݿⱸ�ݹ�������ѡ��
 * 
 * @author hqy 2013-9-26
 * 
 */
public class DBBackupOption {

	private List<String> tableNames;// ��Ҫ���ݵı�������Сд���У�
	private boolean tableStructure;// �Ƿ񵼳���ṹ
	private boolean clearOldData;// �Ƿ����������
	private int daysOfRetention;// ���ݱ�����������Ҫ���������ʱ��Ч��
	private int monthsOfRetention;// ���ݱ�����������Ҫ���������ʱ��Ч��
	private List<String> dateFiledNames;// ʱ���ֶ�������Сд���У���Ŀǰֻ֧���ַ������ֶΣ�����Ҫ���������ʱ��Ч��
	private List<String> dateFiledFormats;// ʱ���ֶθ�ʽ����Ҫ���������ʱ��Ч��
	private String backupFileName;// ���ݺ�ѹ�����ļ��ľ���·����ϵͳ�Զ����ʱ�����ѹ����

	public DBBackupOption() {
		super();
	}

	public DBBackupOption(List<String> tableNames, boolean tableStructure,
			boolean clearOldData, int daysOfRetention, int monthsOfRetention,
			List<String> dateFiledNames, List<String> dateFiledFormats,
			String backupFileName) {
		super();
		this.tableNames = tableNames;
		this.tableStructure = tableStructure;
		this.clearOldData = clearOldData;
		this.daysOfRetention = daysOfRetention;
		this.monthsOfRetention = monthsOfRetention;
		this.dateFiledNames = dateFiledNames;
		this.dateFiledFormats = dateFiledFormats;
		this.backupFileName = backupFileName;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

	public boolean isTableStructure() {
		return tableStructure;
	}

	public void setTableStructure(boolean tableStructure) {
		this.tableStructure = tableStructure;
	}

	public boolean isClearOldData() {
		return clearOldData;
	}

	public void setClearOldData(boolean clearOldData) {
		this.clearOldData = clearOldData;
	}

	public int getDaysOfRetention() {
		return daysOfRetention;
	}

	public void setDaysOfRetention(int daysOfRetention) {
		this.daysOfRetention = daysOfRetention;
	}

	public List<String> getDateFiledNames() {
		return dateFiledNames;
	}

	public void setDateFiledNames(List<String> dateFiledNames) {
		this.dateFiledNames = dateFiledNames;
	}

	public List<String> getDateFiledFormats() {
		return dateFiledFormats;
	}

	public void setDateFiledFormats(List<String> dateFiledFormats) {
		this.dateFiledFormats = dateFiledFormats;
	}

	public String getBackupFileName() {
		return backupFileName;
	}

	public void setBackupFileName(String backupFileName) {
		this.backupFileName = backupFileName;
	}

	public int getMonthsOfRetention() {
		return monthsOfRetention;
	}

	public void setMonthsOfRetention(int monthsOfRetention) {
		this.monthsOfRetention = monthsOfRetention;
	}

}
