package com.talent.common.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.talent.common.constant.ConstantValues;

public class JdbcProperties {
	public static Properties getPropObjFromFile() {
		Properties objProp = new Properties();
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		//classes根路径
		URL url = classLoader.getResource(ConstantValues.JDBC_PROPERTIES_FILE);
		if (url == null) {
			classLoader = ClassLoader.getSystemClassLoader();
			url = classLoader.getResource(ConstantValues.JDBC_PROPERTIES_FILE);
		}
		File file = new File(url.getFile());
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			objProp.load(inStream);
		} catch (FileNotFoundException e) {
			objProp = null;
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
					inStream = null;
				}
			} catch (Exception e) {
			}
		}
		return objProp;
	}
}