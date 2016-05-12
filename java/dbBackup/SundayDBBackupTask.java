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
 * ���ݿⱸ������
 * @author caib 2015-04-21
 *
 */
public class SundayDBBackupTask {
	private Logger logger = Logger.getLogger(this.getClass());
	/**
	 * �������������ڶ�ʱ����ִ�н���Ǽǣ�ֵӦ��CronTriggerBean��spring bean��id��ͬ��
	 * !��com.yjt.gcss.common.advice.TimerTaskResultAdviceͨ�������ȡ�����������ͬʱ��Ҫ�ṩgetter��setter!
	 */
	private String triggerNameForTimerTaskResultAdvice;
	/**
	 * ���ݿⱸ��ѡ��
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
	 * ȫ���������ݣ������������
	 */
	public void DBBackup(){
		Connection conn=null;
		try{
			conn=hibernateTransactionManager.getDataSource().getConnection();
			for(DBBackupOption dBBackupOption : dbBackupOptions){
				List<String> tableNames = dBBackupOption.getTableNames();// ��Ҫ���ݵı�������Сд���У�
				boolean isTableStructure = dBBackupOption.isTableStructure();// �Ƿ񵼳���ṹ
				String backupFileName = dBBackupOption.getBackupFileName();// ���ݺ�ѹ�����ļ��ľ���·����ϵͳ�Զ����ʱ�����ѹ����
				backupFileName += "."+ TimeUtil.getCurDate("yyyyMMddHHmmss");
				/*1.�������ݿ�*/
				DBBackupSqlTool.exportDatabase(backupFileName, conn, isTableStructure, tableNames,null);
				logger.info("�ɹ��������±�:" + tableNames + " �������ļ�[" + backupFileName + "]");
				
				/*2. �ļ�ѹ��*/
				ZipUtil.zipFile(backupFileName, backupFileName + ".zip");
				logger.info("ѹ�������ļ��ɹ�");
				/* 3��ɾ��ѹ��ǰ�ļ� */
				File backupFile = new File(backupFileName);
				if (!backupFile.delete()) {
					logger.error("ɾ��ѹ��ǰ�ļ�[" + backupFileName + "]ʧ��");
				}				
				/*���ݵ����ݱ��ݻ��ϱ�*/
				dBBackupGateway.submitBackupFile(new File(backupFileName+".zip"));
			}
		}catch (Throwable e) {
			logger.error("���ݿⱸ�ݳ���", e);
			TimerTaskException te = new TimerTaskException(e);
			te.setFailMessage(e.getMessage());
			throw te;
		} finally {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (Exception e) {
				logger.error("�ر����ݿ����ӳ���", e);
			}
		}
		
	}
	
	

}
