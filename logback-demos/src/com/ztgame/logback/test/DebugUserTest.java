package com.ztgame.logback.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;

public class DebugUserTest {
	private static Logger logger = LoggerFactory.getLogger(DebugUserTest.class);

	public static void loadConfig() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		try {
			String path = CommonTest.class.getResource("/").getFile();
			configurator.doConfigure(path + "logback-debuguser.xml");
		} catch (JoranException e) {
			e.printStackTrace();
		}
		// StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
	}

	/**
	 * 对单个用户debug，这里用自定义的过滤器来实现（还可以用求值过滤器，但需要引入第三方的jar包）
	 * @param args
	 */
	public static void main(String[] args) {
		loadConfig();
		System.out.println(FilterReply.ACCEPT.compareTo(FilterReply.DENY));
		while (true) {
			logger.debug("userid=128,xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			logger.debug("userid=122,xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			logger.debug("userid=121,xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			logger.debug("userid=2,xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			logger.debug("userid=4,xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
