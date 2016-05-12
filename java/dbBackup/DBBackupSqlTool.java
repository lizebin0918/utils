/**
 * ֻ��MySQL�в��Թ����ɽ����ݿ��е����ݵ�����ָ���ļ���Ҳ�ɴӰ���insert�����ļ��е������ݵ����ݿ⡣
 * ȥ�������ķ���exportDatas()�е�ע�Ϳɽ���ṹ��Ҳ������
 */
package com.yjt.gcss.timerTask.dbBackup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * ���ݿ⵼�뵼������
 * 
 * @author ������
 * @version 1.1.01
 * @date 2014-8-12
 * 
 */
public class DBBackupSqlTool {
	private static Logger logger = Logger.getLogger(DBBackupSqlTool.class);
	
	private static String lineSeparator = System.getProperty("line.separator");
	
	private DBBackupSqlTool() {
	}

	/**
	 * �����ݵ������ļ�
	 * 
	 * @param backupFile
	 *            �����ļ��ľ���·��
	 * @param conn
	 *            ���ݿ�����
	 * @param isTableStructure
	 *            �Ƿ񵼳���ṹ
	 * @param tableNames
	 *            ��Ҫ�����ı�����nullʱ�������ݿ������б�
	 * @param Date
	 * 			  �������������          
	 */
	public static void exportDatabase(String backupFile, Connection conn, boolean isTableStructure, List<String> tableNames, String date) {
		logger.info("export database start...");
		
		ResultSet rs = null;
		Statement stmt = null;
		Writer osw = null;
//		StringBuffer resultString = new StringBuffer();// ��append�������ݺ��������ݹ�ʽΪ��length * 2 + 2��ִ��new char[newLength]ʱ��������޶ȶ���OutOfMemoryError
		try {
			osw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(backupFile), "UTF-8"));
			
			if (null == tableNames) {
				// ��ȡ���б���
				logger.info("getting all table names");
				tableNames = DBBackupSqlTool.getAllTableNames(conn, rs);
			}

			// �������ݿ�
			DBBackupSqlTool.exportDatabase(tableNames, "", conn, stmt, rs, osw, isTableStructure, date);

			// д�ļ�
			logger.info("export database successed, begin to write into file..");
//			osw = new OutputStreamWriter(new FileOutputStream(backupFile),
//					"UTF-8");
//			osw.append(resultString);
			osw.flush();
			// System.out.println("backup is complete!!!");
		} catch (Exception e) {
			logger.error("exporting database failed:", e);
		} finally {
			try {
				if (osw != null) {
					osw.close();
				}
			} catch (IOException e1) {
				logger.error("closing OutputStreamWriter failed:", e1);
			}
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("closing ResultSet failed:", e);
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.error("closing Statement failed:", e);
			}
			
			logger.info("export database end...");
		}
	}
	
	/**
	 * �����ݵ������ļ�
	 * 
	 * @param backupFile
	 *            �����ļ��ľ���·��
	 * @param driver
	 *            �������ݿ��������ƣ��磺"com.mysql.jdbc.Driver"
	 * @param url
	 *            �������ݿ��ַ���磺"jdbc:mysql://localhost:3306/rmdbs"
	 * @param username
	 *            ��¼���ݿ���û���
	 * @param password
	 *            ��¼���ݿ������
	 * @param isTableStructure
	 *            �Ƿ񵼳���ṹ
	 * @param tableNames
	 *            ��Ҫ�����ı�����nullʱ�������ݿ������б�
	 */
	public static void exportDatabase(String backupFile, String driver,
			String url, String username, String password, boolean isTableStructure, List<String> tableNames) {
		logger.info("export database start...");
		
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		Writer osw = null;
//		StringBuffer resultString = new StringBuffer();// ��append�������ݺ��������ݹ�ʽΪ��length * 2 + 2��ִ��new char[newLength]ʱ��������޶ȶ���OutOfMemoryError
		try {
			osw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(backupFile), "UTF-8"));
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);

			if (null == tableNames) {
				// ��ȡ���б���
				logger.info("getting all table names");
				tableNames = DBBackupSqlTool.getAllTableNames(conn, rs);
			}

			// �������ݿ�
			DBBackupSqlTool.exportDatabase(tableNames, url, conn, stmt, rs, osw, isTableStructure,null);

			// д�ļ�
			logger.info("export database successed, begin to write into file..");
//			osw = new OutputStreamWriter(new FileOutputStream(backupFile),
//					"UTF-8");
//			osw.append(resultString);
			osw.flush();
			// System.out.println("backup is complete!!!");
		} catch (Exception e) {
			logger.error("exporting database failed:", e);
		} finally {
			try {
				if (osw != null) {
					osw.close();
				}
			} catch (IOException e1) {
				logger.error("closing OutputStreamWriter failed:", e1);
			}
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("closing ResultSet failed:", e);
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.error("closing Statement failed:", e);
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("closing Connection failed:", e);
			}
			
			logger.info("export database end...");
		}
	}
	
	/**
	 * ��ȡ���ݿ������б�ı���
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private static List<String> getAllTableNames(Connection conn, ResultSet rs) throws SQLException {
		List<String> tableNames = new ArrayList<String>();
		
		// Get tables
		DatabaseMetaData dmd = conn.getMetaData();
		rs = dmd.getTables(null, null, "%", null);
		while (rs.next()) {
			tableNames.add(rs.getString(3));
		}
		rs.close();
		
		return tableNames;
	}

	/**
	 * �������ݿ⣬�ɸ��ݲ���������ṹ�ͱ�����
	 * @param tableNames ��Ҫ�����ı���
	 * @param url
	 * @param conn
	 * @param stmt
	 * @param rs
	 * @param resultString
	 * @param isTableStructure �Ƿ񵼳���ṹ
	 * @param date ����ָ�����ڵ�����
	 * @throws SQLException
	 */
	private static void exportDatabase(List<String> tableNames, String url, Connection conn, 
			Statement stmt, ResultSet rs, Writer resultString, boolean isTableStructure, String date) throws Exception {
		resultString.append("/*").append(lineSeparator);
		resultString.append("MySQL Data Transfer").append(lineSeparator);
		resultString.append("url: ").append(url).append(lineSeparator);
		resultString.append("Date: ").append(new Date().toString()).append(lineSeparator);
		resultString.append("*/").append(lineSeparator).append(lineSeparator);
		 
		resultString.append("SET FOREIGN_KEY_CHECKS=0;").append(lineSeparator).append(lineSeparator);
		
		stmt = conn.createStatement();
		
		if (isTableStructure) {
			// ������ṹ
			logger.info("exporting table structure..");
			DBBackupSqlTool.exportTableStructure(tableNames, stmt, rs, resultString);
		}
		
		// ����������
		logger.info("exporting datas..");
		DBBackupSqlTool.exportDatas(tableNames, stmt, rs, resultString, date);
	}
	
	/**
	 * ������ṹ
	 * @param tableNames
	 * @param stmt
	 * @param rs
	 * @param resultString
	 * @throws SQLException
	 */
	private static void exportTableStructure(List<String> tableNames, Statement stmt, ResultSet rs, Writer resultString) throws Exception {
		for (String tableName : tableNames) {
			resultString.append("-- ----------------------------").append(lineSeparator);
			resultString.append("-- Table structure for ").append(tableName).append(lineSeparator);
			resultString.append("-- ----------------------------").append(lineSeparator);
			resultString.append("DROP TABLE ").append(tableName).append(";").append(lineSeparator);
			rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
			while (rs.next()) {
				// JDBC is 1-based, Java is not !?
				resultString.append(rs.getString(2)).append(";").append(lineSeparator).append(lineSeparator);
			}
			
			rs.close();
		}
	}
	
	/**
	 * ����������
	 * @param tableNames
	 * @param stmt
	 * @param rs
	 * @param resultString
	 * @param date ����ָ�����ڵ�����
	 * @throws SQLException
	 */
	private static void exportDatas(List<String> tableNames, Statement stmt, ResultSet rs, Writer resultString, String date) throws Exception {
		/*
		 * �����������ܽ�������ݲ�ѯ��OutOfMemory�Ĵ���
		 */
		stmt.setFetchSize(Integer.MIN_VALUE);
		stmt.setFetchDirection(ResultSet.FETCH_REVERSE);
		
		resultString.append("-- ----------------------------").append(lineSeparator);
		resultString.append("-- Records").append(lineSeparator);
		resultString.append("-- ----------------------------").append(lineSeparator);
		 
		for (String tableName : tableNames) {

			logger.info("Dumping datas for table " + tableName);

			//resultString.append("DELETE FROM ").append(tableName).append(";").append(lineSeparator);
			if(StringUtils.isNotBlank(date)){
				rs = stmt.executeQuery("SELECT * FROM " + tableName+"  WHERE CREATE_DATE='"+date+"'");
			}else{
				rs = stmt.executeQuery("SELECT * FROM " + tableName);
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				resultString.append("INSERT INTO ").append(tableName).append(" VALUES(");
				// JDBC is 1-based, Java is not !
				for (int col = 1; col <= rsmd.getColumnCount(); col++) {
					resultString.append("'");
					if (rs.getString(col) == null) {
						resultString.append("");
					} else {
						resultString.append(rs.getString(col).replaceAll(
								"[\\r]?[\\n]", "\\\\r\\\\n"));
					}
					if (col == rsmd.getColumnCount()) {
						resultString.append("'");
					} else {
						resultString.append("', ");
					}
				}
				resultString.append(");").append(lineSeparator);
			}
			resultString.append(lineSeparator);
			rs.close();
		}
	}
	
	/**
	 * �������ݵ����ݿ�
	 * <p>
	 * <b>ע�⣺�÷�������Ա���������MysqlTool.exportDatas()�����������ļ����лָ����ݶ�����������֤ͨ������ȷ�ԣ�</b>
	 * </p>
	 * 
	 * @param input
	 *            ���������ļ���������
	 * @param driver
	 *            �������ݿ��������ƣ��磺"com.mysql.jdbc.Driver"
	 * @param url
	 *            �������ݿ��ַ���磺"jdbc:mysql://localhost:3306/rmdbs"
	 * @param username
	 *            ��¼���ݿ���û���
	 * @param password
	 *            ��¼���ݿ������
	 */
	public static void importDatas(BufferedReader input, String driver,
			String url, String username, String password) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();

			String sql = "";
			do {
				sql = input.readLine();
				if (StringUtils.isBlank(sql)) {
					sql = input.readLine();
				}
				if (StringUtils.isBlank(sql)) {
					sql = input.readLine();
				}
				if (StringUtils.isBlank(sql)) {
					sql = input.readLine();
				} else {
					System.out.println(sql);
					stmt.execute(sql.trim());
				}
			} while (!StringUtils.isBlank(sql));

			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.error("Closing Statement failed:", e);
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("Closing Connection failed:", e);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void testMain(String[] args) {
		String backupFile = "d://mysqldump.sql";
		String driver = "org.mariadb.jdbc.Driver";
		String url = "jdbc:mariadb://56.16.32.129:3306/yjtorgfep1";
		String username = "yjtorgfep1";
		String password = "yjtorgfep1";
		
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("BT_TIMER_TASK");

		DBBackupSqlTool.exportDatabase(backupFile, driver, url, username, password, true, tableNames);
	}
}
