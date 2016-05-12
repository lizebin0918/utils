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
 * ���ݿⱸ������
 * @author caib 2015-04-21
 *
 */
public class YearDBBackupTask {
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
	 * �����������ݣ������������
	 */
	public void DBBackup(){
		//ʹ��java���뱸��
		//javaDBBackup();
		//ʹ�ýű�����
		shellDBBackup();
	}
	
	public boolean shellDBBackup(){
		logger.info("ִ�нű���������ʼ��");
		if(StringUtils.isBlank(shellFile)){
			logger.info("��ʱ����ִ��ʧ��,���ݽű�������");
		}
		/*1. �ı�ִ�нű���Ȩ��*/
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
	 * ʹ��java���뱸��
	 */
	public void javaDBBackup(){

		Connection conn=null;
		try{
			conn=hibernateTransactionManager.getDataSource().getConnection();
			for(DBBackupOption dBBackupOption : dbBackupOptions){
				List<String> tableNames = dBBackupOption.getTableNames();// ��Ҫ���ݵı�������Сд���У�
				boolean isTableStructure = dBBackupOption.isTableStructure();// �Ƿ񵼳���ṹ
				String backupFileName = dBBackupOption.getBackupFileName();// ���ݺ�ѹ�����ļ��ľ���·����ϵͳ�Զ����ʱ�����ѹ����
				backupFileName += "." + TimeUtil.getCurDate("yyyyMMddHHmmss");
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
