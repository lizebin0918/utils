package com.talent.common.util.xml.convertion;


import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DefinedPrettyPrintWriter implements ExtendedHierarchicalStreamWriter {

    private final QuickWriter writer;
    private final FastStack elementStack = new FastStack(16);
    private final char[] lineIndenter;

    private boolean tagInProgress;
    private int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private boolean breakLine = false; // 是否换行

    private static final char[] AMP = "&".toCharArray();
    private static final char[] LT = "<".toCharArray();
    private static final char[] GT = ">".toCharArray();
    private static final char[] SLASH_R = " ".toCharArray();
    private static final char[] QUOT = "\"".toCharArray();
    private static final char[] APOS = "'".toCharArray();
    private static final char[] CLOSE = "</".toCharArray();

    private static final String CDATA_PREFIX = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private boolean isNeedCDATA;
    private List<String> cdataNames;
    
    public DefinedPrettyPrintWriter(Writer writer, char[] lineIndenter, boolean breakLine, boolean needXMLHead, String encoding, List<String> cdataNames) throws IOException {
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter;
        this.breakLine = breakLine;
        this.cdataNames = cdataNames;

        // 增加XML头
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"" + (StringUtils.isNotBlank(encoding) ? encoding : "GBK") + "\"?>";
        if (needXMLHead && breakLine) {
            writer.write(xmlHeader + "\n");
        }

        if (needXMLHead && !breakLine) {
            writer.write(xmlHeader);
        }
    }

    public DefinedPrettyPrintWriter(Writer writer, boolean breakLne, boolean needXMLHead, String encoding, List<String> cdataNames) throws IOException {
        // this(writer, new char[] { ' ', ' ' }); // 节点之间以空格分隔
        this(writer, breakLne ? new char[] { ' ', ' ' } : new char[] {}, breakLne, needXMLHead, encoding, cdataNames);
    }

    public DefinedPrettyPrintWriter(Writer writer, boolean breakLne) throws IOException {
        // this(writer, new char[] { ' ', ' ' }); // 节点之间以空格分隔
        this(writer, breakLne ? new char[] { ' ', ' ' } : new char[] {}, breakLne, true, "GBK", null);
    }

    public void startNode(String name) {
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(name);
        elementStack.push(name);
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    @SuppressWarnings("unchecked")
    public void startNode(String name, Class clazz) {
        startNode(name);
        if (null != cdataNames) {
        	isNeedCDATA = cdataNames.contains(name);
        }
    }

    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();

        writeText(writer, text);
    }

    public void addAttribute(String key, String value) {
        writer.write(' ');
        writer.write(key);
        writer.write('=');
        writer.write('"');
        writeAttributue(writer, value);
        writer.write('"');
    }

    protected void writeAttributue(QuickWriter writer, String text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&':
                    this.writer.write(AMP);
                    break;
                case '<':
                    this.writer.write(LT);
                    break;
                case '>':
                    this.writer.write(GT);
                    break;
                case '"':
                    this.writer.write(QUOT);
                    break;
                case '\'':
                    this.writer.write(APOS);
                    break;
                case '\r':
                    this.writer.write(SLASH_R);
                    break;
                default:
                    this.writer.write(c);
            }
        }
    }

    protected void writeText(QuickWriter writer, String text) {
        int length = text.length();
        if (isNeedCDATA) {
        	writer.write(CDATA_PREFIX);
            for (int i = 0; i < length; i++) {
                char c = text.charAt(i);
                switch (c) {
                    case '&':
                        this.writer.write(AMP);
                        break;
                    case '<':
                        this.writer.write(LT);
                        break;
                    case '>':
                        this.writer.write(GT);
                        break;
                    case '"':
                        this.writer.write(QUOT);
                        break;
                    case '\'':
                        this.writer.write(APOS);
                        break;
                    case '\r':
                        this.writer.write(SLASH_R);
                        break;
                    default:
                        this.writer.write(c);
                }
            }
            writer.write(CDATA_END);
        }

        else {
            for (int i = 0; i < length; i++) {
                char c = text.charAt(i);
                this.writer.write(c);
            }
        }
    }

    public void endNode() {
        depth--;
        if (tagIsEmpty) {
            writer.write('/');
            readyForNewLine = false;
            finishTag();
            elementStack.popSilently();
        } else {
            finishTag();
            writer.write(CLOSE);
            writer.write((String) elementStack.pop());
            writer.write('>');
        }
        readyForNewLine = true;
        if (depth == 0) {
            writer.flush();
        }
    }

    private void finishTag() {
        if (tagInProgress) {
            writer.write('>');
        }
        tagInProgress = false;
        if (readyForNewLine) {
            endOfLine();
        }
        readyForNewLine = false;
        tagIsEmpty = false;
    }

    protected void endOfLine() {
        if (breakLine) {// 节点是否换行，不换所有节点一行显示
            writer.write('\n');
        }
        for (int i = 0; i < depth; i++) {
            writer.write(lineIndenter);
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

}

