package com.ztgame.logback.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class One2OneTest {
	private static Logger logger = LoggerFactory.getLogger(One2OneTest.class);

	public static void roleInfo(int index) {
		MDC.put("userid", "" + index);
		Thread.yield();
		logger.info("userid{}", index);
		MDC.put("userid", null);
	}

	public static void loadConfig() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		try {
			String path = CommonTest.class.getResource("/").getFile();
			configurator.doConfigure(path + "logback-one2one.xml");
		} catch (JoranException e) {
			e.printStackTrace();
		}
		// StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
	}

	/**
	 * 每个玩家对应一个log文件
	 * @param args
	 */
	public static void main(String[] args) {
		loadConfig();
		int i = 1;
		while (true) {
			roleInfo(i++);
			i = (i == 5 ? 1 : i);
		}
	}

}
