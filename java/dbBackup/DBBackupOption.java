package com.yjt.gcss.timerTask.dbBackup;

import java.util.List;

/**
 * 数据库备份功能配置选项
 * 
 * @author hqy 2013-9-26
 * 
 */
public class DBBackupOption {

	private List<String> tableNames;// 需要备份的表名（大小写敏感）
	private boolean tableStructure;// 是否导出表结构
	private boolean clearOldData;// 是否清理旧数据
	private int daysOfRetention;// 数据保留天数（需要清理旧数据时有效）
	private int monthsOfRetention;// 数据保留月数（需要清理旧数据时有效）
	private List<String> dateFiledNames;// 时间字段名（大小写敏感）（目前只支持字符串的字段）（需要清理旧数据时有效）
	private List<String> dateFiledFormats;// 时间字段格式（需要清理旧数据时有效）
	private String backupFileName;// 备份后压缩包文件的绝对路径（系统自动添加时间戳并压缩）

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
