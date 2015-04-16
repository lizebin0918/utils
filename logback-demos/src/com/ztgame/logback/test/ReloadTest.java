package com.ztgame.logback.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class ReloadTest {
	private static Logger logger = LoggerFactory.getLogger(ReloadTest.class);

	public static void loadConfig() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		try {
			String path = CommonTest.class.getResource("/").getFile();
			configurator.doConfigure(path + "logback-reload.xml");
		} catch (JoranException e) {
			e.printStackTrace();
		}
		// StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadConfig();
		while (true) {
			logger.debug("");
		}
	}

}
