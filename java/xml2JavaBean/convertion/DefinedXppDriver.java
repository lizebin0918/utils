package com.talent.common.util.xml.convertion;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import sun.rmi.runtime.Log;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class DefinedXppDriver extends XppDriver {
   // public static final Log logger = LogFactory.getLog(DefinedXppDriver.class);

    private boolean breakLine = false; // 是否换行

    private boolean needXmlHeader = true;// 是否需要报文头

    private String encoding = "GBK"; // xml头编码方式
    
    /**
     * 需要添加CDATA信息的节点名称
     */
    private List<String> cdataNames;

    /**
     * 
     * @param breakLine 是否格式化
     * @param needXmlHeader 是否需要报文头
     * @param encoding 报文头编码
     */
    public DefinedXppDriver(boolean breakLine, boolean needXmlHeader, String encoding, List<String> cdataNames) {
        this.breakLine = breakLine;
        this.needXmlHeader = needXmlHeader;
        this.encoding = encoding;
        this.cdataNames = cdataNames;
    }

    public DefinedXppDriver() {
        this(false, true, "GBK", null);
    }

    @Override
    public HierarchicalStreamWriter createWriter(Writer out) {
        HierarchicalStreamWriter writer = null;
        try {
            writer = new DefinedPrettyPrintWriter(out, breakLine, needXmlHeader, encoding, cdataNames);
        } catch (IOException e) {
            //logger.error(e);
        }
        return writer;
    }
}

