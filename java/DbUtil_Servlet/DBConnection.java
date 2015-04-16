package com.talent.common.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.talent.common.constant.ConstantValues;

public class DBConnection {
	private static Logger logger = Logger.getLogger(DBConnection.class);
	private static DataSource datasource = null;
	private DBConnection() {
		
	}

	static {
		try {
			Properties prop = null;
			//获取配置文件信息
			prop = JdbcProperties.getPropObjFromFile();
			//创建连接池
			datasource = new DataSource();
			PoolProperties p = new PoolProperties();
			p.setRemoveAbandoned(true);
			p.setRemoveAbandonedTimeout(120);
			p.setTestOnBorrow(true);
			p.setLogAbandoned(true);
			p.setUrl(prop.getProperty(ConstantValues.DB_CONNECTION_URL));
			p.setDriverClassName(prop.getProperty(ConstantValues.DB_DRIVER_CLASS_NAME));
			p.setUsername(prop.getProperty(ConstantValues.DB_USER_NAME));
			p.setPassword(prop.getProperty(ConstantValues.DB_USER_PASSWORD));
			p.setJmxEnabled(true);
			p.setTestWhileIdle(false);
			p.setTestOnBorrow(true);
			p.setValidationQuery("SELECT 1");
			p.setTestOnReturn(false);
			p.setValidationInterval(30000);
			p.setTimeBetweenEvictionRunsMillis(30000);
			p.setMaxActive(100);
			p.setInitialSize(20);
			p.setMaxWait(10000);
			p.setRemoveAbandonedTimeout(60);
			p.setMinEvictableIdleTimeMillis(30000);
			p.setMinIdle(10);
			p.setLogAbandoned(true);
			p.setRemoveAbandoned(true);
			p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
					+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
			datasource.setPoolProperties(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connection不能保证线程安全不能调用此方法获取连接
	 * @return Connection
	 * */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("系统获取数据库连接错误");
			e.printStackTrace();
		}
		return conn;
	}
}