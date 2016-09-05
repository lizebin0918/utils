package net.sf.log4jdbc;

import java.sql.Connection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author liqd 适用于log4jdbc 1.2
 *         
 *         包名必须为:net.sf.log4jdbc
 *         
 *         xml 配置:
 *         <bean id="log4jdbcInterceptor"
 *         class="net.sf.log4jdbc.DataSourceSpyInterceptor" />
 * 
 *         <bean id="dataSourceLog4jdbcAutoProxyCreator" class=
 *         "org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator"
 *         > <property name="interceptorNames"> <list>
 *         <value>log4jdbcInterceptor</value> </list> </property> <property
 *         name="beanNames"> <list> <value>dataSource</value> </list>
 *         </property> </bean>
 * 
 *         spring boot 配置详见:Log4jdbcConfig.java
 *         
 *         日志信息如果全部为off,log4jdbc将不会生效,因此对性能没有任何影响 log4j.properties
 *         log4j.logger.jdbc.sqlonly=ON
 *         log4j.logger.jdbc.sqltiming=OFF
 *         log4j.logger.jdbc.audit=OFF 
 *		   log4j.logger.jdbc.resultset=OFF
 *         log4j.logger.jdbc.connection=OFF
 * 
 */
public class DataSourceSpyInterceptor implements MethodInterceptor {

	private RdbmsSpecifics rdbmsSpecifics = null;

	private RdbmsSpecifics getRdbmsSpecifics(Connection conn) {
		if (rdbmsSpecifics == null) {
			rdbmsSpecifics = DriverSpy.getRdbmsSpecifics(conn);
		}
		return rdbmsSpecifics;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object result = invocation.proceed();
		if (SpyLogFactory.getSpyLogDelegator().isJdbcLoggingEnabled()) {
			if (result instanceof Connection) {
				Connection conn = (Connection) result;
				return new ConnectionSpy(conn, getRdbmsSpecifics(conn));
			}
		}
		return result;
	}

}
