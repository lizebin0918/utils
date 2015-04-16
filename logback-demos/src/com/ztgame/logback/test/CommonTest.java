package com.ztgame.logback.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * logback一些常规的用法
 * 
 * @author wuxiaofei
 */
public class CommonTest {
	private static final Logger logger = LoggerFactory
			.getLogger(CommonTest.class);

	public static void main(String[] args) {
		// while (true) {
		logger.trace("do trace");
		logger.debug("do debug");
		logger.info("do info");
		logger.warn("do warn");
		logger.error("do error");
		// }
	}
}
