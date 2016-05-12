package com.yjt.gcss.timerTask.dbBackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
public class YearDBBackupTask {
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
	private String shellFile;
	
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

	public void setShellFile(String shellFile) {
		this.shellFile = shellFile;
	}

	/**
	 * 增量备份数据，并不清除数据
	 */
	public void DBBackup(){
		//使用java代码备份
		//javaDBBackup();
		//使用脚本备份
		shellDBBackup();
	}
	
	public boolean shellDBBackup(){
		logger.info("执行脚本备份任务开始！");
		if(StringUtils.isBlank(shellFile)){
			logger.info("定时任务执行失败,备份脚本不存在");
		}
		/*1. 改变执行脚本的权限*/
		String command1 = "chmod 777 " + shellFile;
		Process process =null;
		try {
			process=Runtime.getRuntime().exec(command1);
			process.waitFor();
			Runtime.getRuntime().exec("bash "+shellFile);
			int iretCode = process.waitFor();
			BufferedReader br=null;
			try{
                if (iretCode != 0) 
                { 
                	br = new BufferedReader( 
                            new InputStreamReader(process.getErrorStream())); 
                    StringBuilder errorDesc = new StringBuilder(); 
                    for (String str = br.readLine(); str != null; str = br.readLine()){ 
                    	errorDesc.append(str);
                    } 
                    logger.error("execute shell " + shellFile + " failed: " 
                            + errorDesc); 
                }else{
                	return true;
                }
			}catch(IOException e){
				 logger.error("IOException:", e); 
			}finally{
				if(br!=null){
                	br.close();
                }
                process.getErrorStream().close(); 
                process.getInputStream().close(); 
                process.getOutputStream().close();
            }
		} catch (Exception e) {
			logger.error("execute"+shellFile+"failed", e);
		}
		return false;
	}
	
	/**
	 * 使用java代码备份
	 */
	public void javaDBBackup(){

		Connection conn=null;
		try{
			conn=hibernateTransactionManager.getDataSource().getConnection();
			for(DBBackupOption dBBackupOption : dbBackupOptions){
				List<String> tableNames = dBBackupOption.getTableNames();// 需要备份的表名（大小写敏感）
				boolean isTableStructure = dBBackupOption.isTableStructure();// 是否导出表结构
				String backupFileName = dBBackupOption.getBackupFileName();// 备份后压缩包文件的绝对路径（系统自动添加时间戳并压缩）
				backupFileName += "." + TimeUtil.getCurDate("yyyyMMddHHmmss");
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
}
