package com.yaodian.cms.config;

import net.sf.log4jdbc.DataSourceSpyInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * spring boot 配置
 * @author lizebin
 * 
 */
@Configuration
public class Log4jdbcConfig {
	private static Logger log = LoggerFactory.getLogger(Log4jdbcConfig.class);
	
	@Bean(name = "log4jdbcCreator")
    public BeanNameAutoProxyCreator log4jdbcCreator() {
		BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
		creator.setInterceptorNames("log4jdbcInterceptor");
		creator.setBeanNames("master");
		return creator;
    }
	
	@Bean(name = "log4jdbcInterceptor")
    public DataSourceSpyInterceptor log4jdbcInterceptor() {
		return new DataSourceSpyInterceptor();
    }
	
}
