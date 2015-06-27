package com.talent.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 * @author 李泽彬
 * 数据库关闭顺序:ResultSet-->Statement or PreparedStatement --> Connection
 * 
 * */
public final class DbUtil {

	private static ThreadLocal<Connection> connections = new ThreadLocal<Connection>();

    /**
     * 开启事务
     * @author 李泽彬
     * */
	public static void beginTransaction() throws SQLException {
		Connection conn = connections.get();
		if (conn == null) {
			conn = DBConnection.getConnection();
			connections.set(conn);
		}
		if (conn.getAutoCommit()) {
			conn.setAutoCommit(false);
		}
	}

	/**
	 * 获取Connection连接
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		Connection conn = connections.get();
		if (conn == null) {
			conn = DBConnection.getConnection();
			connections.set(conn);
		}
		return conn;
	}

	/**
	 * 关闭连接(关闭之前请注意关闭顺序)
	 * @throws SQLException
	 */
	public static void closeConnection() throws SQLException {
		Connection conn = connections.get();
		if(conn != null) {
			conn.setAutoCommit(true);
			conn.close();
			connections.remove();
			connections.set(null);
		}
	}

	/**
	 * 提交事务
	 * @throws SQLException
	 */
	public static void commitTransaction() throws SQLException {
		Connection conn = connections.get();
		if(conn != null && !conn.getAutoCommit()) {
			conn.commit();
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 回滚事务
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException {
		Connection conn = connections.get();
		if(conn != null && !conn.getAutoCommit()) {
			conn.rollback();
			conn.setAutoCommit(true);
		}
	}

	
	/**
	 * 只能用于类型为整形的主键
	 * @param stmtSql
	 * 		完整的sql语句，没有占位符
	 * @return
	 */
	public static long addBeanRetPK(String stmtSql) throws SQLException {
		long id = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(stmtSql, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
			id = rs.next() ? rs.getInt(1) : id;
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			//在业务层的代理类会最后关闭Connection连接
			/*closeConnection();*/
		}

		return id;
	}
	/**
	 * 只能用于类型为整形的主键
	 * @param pstmtSql
	 * 		带占位符的sql语句, 如: insert into tableName values (?,?,?)
	 * @param values
	 * 		占位符多对应的值，按顺序存储
	 * @return
	 * @throws SQLException
	 */
	public static long addBeanRetPK(String pstmtSql, ArrayList<String> values) throws SQLException {
		long id = 0;
		Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(pstmtSql, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = null;
		try {
			if(values != null) {
				for(int i=1; i<=values.size(); i++) {
					pstmt.setString(i, values.get(i - 1));
				}
			}
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			id = rs.next() ? rs.getInt(1) : id;
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
			//在业务层的代理类会最后关闭Connection连接
			/*closeConnection();*/
		}
		return id;
	}
	
	public static void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closePreparedStatement(PreparedStatement pstmt) {
		try {
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
