package com.yjt.gcss.timerTask.dbBackup;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.yjt.common.tools.TimeUtil;
import com.yjt.gcss.common.exception.TimerTaskException;
import com.yjt.gcss.timerTask.DBBackupGateway;
import com.yjt.gcss.timerTask.fileBackup.ZipUtil;
/**
 * 数据库备份任务
 * @author caib 2015-04-21
 *
 */
public class MonthDBBackupTask {
	private Logger logger = Logger.getLogger(this.getClass());
	/**
	 * 触发器名，用于定时任务执行结果登记（值应与CronTriggerBean的spring bean的id相同）
	 * !由com.yjt.gcss.common.advice.TimerTaskResultAdvice通过反射获取，请勿改名，同时需要提供getter、setter!
	 */
	private String triggerNameForTimerTaskResultAdvice;
	/**
	 * 数据库备份选项
	 */
	private List<DBBackupOption> dbBackupOptions;
	private HibernateTransactionManager hibernateTransactionManager;
	private DBBackupGateway dBBackupGateway;
	
	public String getTriggerNameForTimerTaskResultAdvice() {
		return triggerNameForTimerTaskResultAdvice;
	}

	public void setTriggerNameForTimerTaskResultAdvice(
			String triggerNameForTimerTaskResultAdvice) {
		this.triggerNameForTimerTaskResultAdvice = triggerNameForTimerTaskResultAdvice;
	}

	public List<DBBackupOption> getDbBackupOptions() {
		return dbBackupOptions;
	}

	public void setDbBackupOptions(List<DBBackupOption> dbBackupOptions) {
		this.dbBackupOptions = dbBackupOptions;
	}

	public HibernateTransactionManager getHibernateTransactionManager() {
		return hibernateTransactionManager;
	}

	public void setHibernateTransactionManager(
			HibernateTransactionManager hibernateTransactionManager) {
		this.hibernateTransactionManager = hibernateTransactionManager;
	}
	

	public DBBackupGateway getdBBackupGateway() {
		return dBBackupGateway;
	}

	public void setdBBackupGateway(DBBackupGateway dBBackupGateway) {
		this.dBBackupGateway = dBBackupGateway;
	}

	/**
	 * 增量备份数据，并不清除数据
	 * 月初备份上个月的数据
	 */
	public void DBBackup(){
		Connection conn=null;
		try{
			conn=hibernateTransactionManager.getDataSource().getConnection();
			for(DBBackupOption dBBackupOption : dbBackupOptions){
				String month=TimeUtil.getPreMonth(new Date());
				List<String> tableNames = dBBackupOption.getTableNames();// 需要备份的表名（大小写敏感）
				boolean isTableStructure = dBBackupOption.isTableStructure();// 是否导出表结构
				String backupFileName = dBBackupOption.getBackupFileName();// 备份后压缩包文件的绝对路径（系统自动添加时间戳并压缩）
				backupFileName += "."+month+"_"+ TimeUtil.getCurDate("yyyyMMddHHmmss");
				/*1.备份数据库*/
				DBBackupSqlTool.exportDatabase(backupFileName, conn, isTableStructure, tableNames,null);
				logger.info("成功备份以下表:" + tableNames + " 到备份文件[" + backupFileName + "]");
				
				/*2. 文件压缩*/
				ZipUtil.zipFile(backupFileName, backupFileName + ".zip");
				logger.info("压缩备份文件成功");
				/* 3、删除压缩前文件 */
				File backupFile = new File(backupFileName);
				if (!backupFile.delete()) {
					logger.error("删除压缩前文件[" + backupFileName + "]失败");
				}
				
				/*备份到数据备份机上边*/
				dBBackupGateway.submitBackupFile(new File(backupFileName+".zip"));
				
				if (dBBackupOption.isClearOldData()) {
					/* 4、清理旧数据 */
					List<String> dateFiledNames = dBBackupOption.getDateFiledNames();// 时间字段名（大小写敏感）（目前只支持字符串的字段）
					List<String> dateFiledFormats = dBBackupOption.getDateFiledFormats();// 时间字段格式
					
					if (tableNames.size() != dateFiledNames.size() || tableNames.size() != dateFiledFormats.size()) {
						// 表名配置与时间字段配置不匹配
						logger.error("表名配置与时间字段配置不匹配，无法继续清理旧数据");
						continue;
					}
					
					 //获取N月前的时间字符串 
					int monthsOfRetention=dBBackupOption.getMonthsOfRetention();
					Date dateRelateToMonth= TimeUtil.getDateRelateToMonth(new Date(), -monthsOfRetention);
					String preDateString = TimeUtil.dateFormat(dateRelateToMonth, "yyyyMM");
					
					for (int i = 0; i < tableNames.size(); i++) {
						String tableName = tableNames.get(i);
						String dateFiledName = dateFiledNames.get(i);
						String dateFiledFormat = dateFiledFormats.get(i);
						
						// 表tableName对应的格式为dateFiledFormat的N天前的时间字符串
						String beforeDate = TimeUtil.changeStrTimeFormat(preDateString, "yyyyMM", dateFiledFormat);
						// 清理N月前数据
						if (this.clearDataBefore(conn, tableName, dateFiledName, beforeDate)) {
							logger.info("清理表[" + tableName + "][" + beforeDate + "]之前的数据成功");
						} else {
							logger.error("清理表[" + tableName + "][" + beforeDate + "]之前的数据失败");
						}
					}
				}
				
			}
		}catch (Throwable e) {
			logger.error("数据库备份出错：", e);
			TimerTaskException te = new TimerTaskException(e);
			te.setFailMessage(e.getMessage());
			throw te;
		} finally {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (Exception e) {
				logger.error("关闭数据库连接出错：", e);
			}
		}
		
	}
	
	/**
	 * 删除表tableName中字段dateFiledName满足条件:"dateFiledName<beforeDate"的所有数据
	 * @param connection
	 * @param tableName
	 * @param dateFiledName
	 * @param beforeDate
	 */
	private boolean clearDataBefore(Connection connection, String tableName, String dateFiledName, String beforeDate) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String sql = "DELETE FROM `" + tableName + "` WHERE `" + dateFiledName + "`<'" + beforeDate + "'";
			logger.info(sql);
			stmt.execute(sql);
			
			return true;
		} catch (Exception e) {
			logger.error("删除数据出错:", e);
			
			return false;
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
			} catch (Exception e) {
				logger.error("closing Statement failed:", e);
			}
		}
	}
	
}
