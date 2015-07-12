package com.yjt.pgs.Tools;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class XMLProcessor
{
  private Document doc;
  private Element root;
  private File xmlFile;

  public XMLProcessor(File xmlFile)
    throws Exception
  {
    this.xmlFile = xmlFile;
    SAXBuilder builder = new SAXBuilder();
    this.doc = builder.build(xmlFile);
    this.root = this.doc.getRootElement();
  }

  public XMLProcessor(String xmlString) throws Exception
  {
    xmlString = xmlString.replaceAll("[\r\n,\r,\n]", "");

    CharArrayReader reader = new CharArrayReader(xmlString.toCharArray());
    SAXBuilder builder = new SAXBuilder();
    this.doc = builder.build(reader);
    this.root = this.doc.getRootElement();
  }

  public void setNodeText(String xPathString, String value) throws Exception {
    setNodeText(xPathString, value, false, "");
  }

  public void setNodeText(String xPathString, String value, boolean required, String requiredName)
    throws Exception
  {
    if ((required) && (Util.stringIsEmpty(value))) {
      throw new Exception(requiredName + "不能为空。");
    }
    Element element = (Element)XPath.selectSingleNode(this.root, xPathString);

    if (element == null) {
      throw new Exception("无法设置结点值:" + xPathString + ",无此结点");
    }
    element.setText(value == null ? "" : value);
  }

  public String getNodeText(String xPathString) throws Exception {
    Element element = (Element)XPath.selectSingleNode(this.root, xPathString);

    if (element == null) {
      throw new Exception("无此结点:" + xPathString);
    }
    return element.getText().trim();
  }

  public Element getRoot() {
    return this.root;
  }

  public void save(String filePath) throws Exception {
    File outFile = null;
    if (filePath != null) {
      outFile = new File(filePath);
      if (outFile != null);
    }
    else {
      outFile = this.xmlFile;
    }
    FileWriter writer = new FileWriter(outFile);

    XMLOutputter out = null;
    try {
      out = new XMLOutputter();
      out.output(this.doc, writer);
    } finally {
      writer.close();
    }
  }

  public List<Element> getNodes(String xPathString) throws Exception {
    return XPath.selectNodes(this.root, xPathString);
  }

  public Element getFirstNode(String xPathString) throws Exception {
    return (Element)XPath.selectSingleNode(this.root, xPathString);
  }

  public void save() throws Exception {
    save(null);
  }

  public void setNodeProp(String xPathString, String propName, String value) throws Exception {
    Element element = (Element)XPath.selectSingleNode(this.root, xPathString);
    if (element == null) {
      throw new Exception("无此结点：" + xPathString);
    }
    setNodeProp(element, propName, value);
  }

  public void setNodeProp(Element element, String propName, String value) throws Exception {
    Attribute attr = element.getAttribute(propName);
    if (attr == null) {
      throw new Exception("无" + propName + "属性");
    }
    element.setAttribute(propName, value);
  }

  public String getNodeProp(String xPathString, String propName) throws Exception {
    Element element = (Element)XPath.selectSingleNode(this.root, xPathString);
    if (element == null) {
      throw new Exception("无此结点：" + xPathString);
    }
    return getNodeProp(element, propName);
  }

  public String getNodeProp(Element element, String propName) throws Exception {
    Attribute attr = element.getAttribute(propName);

    if (attr == null) {
      throw new Exception("无此属性：" + propName);
    }
    return element.getAttributeValue(propName);
  }

  public String getDocText() {
    return getDocText(true, "");
  }

  public String getDocText(String encode) {
    return getDocText(true, encode);
  }

  public String getDocText(boolean needDeclaration) {
    return getDocText(needDeclaration, "");
  }

  public String getDocText(boolean needDeclaration, String encode) {
    XMLOutputter out = new XMLOutputter();
    String returnString = "";

    Format format = out.getFormat();

    if (needDeclaration)
    {
      if (Util.stringIsEmpty(encode))
        format.setEncoding("GBK");
      else {
        format.setEncoding(encode);
      }

      format.setIndent("");
      format.setLineSeparator("");
      out.setFormat(format);

      returnString = out.outputString(this.doc);
    } else {
      returnString = out.outputString(this.doc);

      if (returnString.startsWith("<?xml")) {
        int pos = returnString.indexOf("?>");

        returnString = returnString.substring(pos + 4);
      }
    }

    return returnString;
  }

  public String getChildTextByKeyNode(String keyNodeXPath, String keyName, String keyValue, String childName)
  {
    String xPath = keyNodeXPath + "[child::" + keyName + "=\"" + keyValue + "\"]";
    String result = null;
    try {
      Element element = (Element)XPath.selectSingleNode(this.root, xPath);
      result = element.getChildText(childName);
    } catch (Exception e) {
      result = null;
    }

    return result;
  }
}
