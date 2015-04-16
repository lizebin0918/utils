package com.talent.common.util.xml.convertion;



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.dom4j.io.SAXReader;
import org.dom4j.util.XMLErrorHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.Dom4JReader;

public class Xml2JavaBean {
	
	//private static Log log = LogFactory.getLog(Xml2JavaBean.class);
	
	private static Xml2JavaBean instance = new Xml2JavaBean();
	
	public static Xml2JavaBean getInstance() {
		if(instance == null) {
			return new Xml2JavaBean();
		}
		return instance;
	}
	private static Dom4JDriver dom4jDriver = new Dom4JDriver();
	
	private static XStream xStream;
	
	static {

		xStream = new XStream(dom4jDriver);
	}
	
	private Xml2JavaBean() {}
	
	/**
	 * XML2JavaBean
	 * @param msg
	 * @return
	 */
	public <T> T xml2JavaBean(String msg, Class<T> type) {
		T msgBean = null;
		try {
			aliasClass(type);
			SAXReader saxReader = new SAXReader();
			saxReader.setErrorHandler(new XMLErrorHandler());
			msg = CDATAUtil.executeCDATA(msg);
			InputStream is = new ByteArrayInputStream(msg.getBytes("GBK"));
			InputStreamReader in = new InputStreamReader(is, "GBK");
			
	        Dom4JReader dom4jReader = new Dom4JReader(saxReader.read(in));
	        in.close();
	        is.close();
	        in = null;
	        is = null;
	        
			msgBean = (T) xStream.unmarshal(dom4jReader);
			dom4jReader.close();
			dom4jReader = null;
		} catch (Exception e) {
			e.printStackTrace();
			//log.error("XML转JavaBean报错", e);
		}
		
		return msgBean;
	}
	
	private void aliasClass(Class<?> type) {
		if(type.isInterface() || type.isEnum() || type.getName().equals(Object.class.getName())) {
			return;
		}
		xStream.alias(type.getSimpleName(), type);
		aliasClass(type.getSuperclass());
	}
}
