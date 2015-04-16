package com.talent.common.util.xml.convertion;



import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class JavaBean2XML {

	//private static Logger logger = Logger.getLogger(JavaBean2XML.class);

	private static XStream xStream;
    private static List<String> cdataNames;// xml报文中的CDATA标签名

    private static JavaBean2XML instance = new JavaBean2XML();
	
	public static JavaBean2XML getInstance() {
		if(instance == null) {
			return new JavaBean2XML();
		}
		return instance;
	}
	static {
		cdataNames = new ArrayList<String>();
		xStream = new XStream(new DefinedXppDriver(false, true, "GBK", cdataNames));
	}
	
	private JavaBean2XML() {
	}

	/**
	 * JavaBean2XML
	 * 
	 * @param msg
	 * @return
	 */
	public <T> String javaBean2XML(T bean) {
		try {
			aliasClass(bean.getClass());
			String xmlMsg = xStream.toXML(bean);
			return xmlMsg;
		} catch (Exception e) {
			e.printStackTrace();
			//logger.error("-----Bean生成错误-------", e);
			return null;
		}
	}
	
	private void aliasClass(Class<?> type) {
		if(type.isInterface() || type.isEnum() || type.getName().equals(Object.class.getName())) {
			return;
		}
		xStream.alias(type.getSimpleName(), type);
		aliasClass(type.getSuperclass());
	}
}