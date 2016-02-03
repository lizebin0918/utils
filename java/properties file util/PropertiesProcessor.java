package com.yjt.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import com.yjt.pgs.Tools.Util;

public class PropertiesProcessor
{
  private Properties propFile;
  String filePath = "";

  public PropertiesProcessor(String filePath) throws Exception
  {
    this.filePath = filePath;

    this.propFile = new Properties();
    try
    {
      this.propFile.load(new BufferedInputStream(new FileInputStream(filePath)));
    } catch (Exception e) {
      throw new Exception("加载属性文件" + filePath + "失败!" + e.getMessage());
    }
  }

  public Properties getKeyMap() {
    return this.propFile;
  }

  public void setPropValue(String properties, String value) throws Exception {
    FileReader p = null;
    BufferedReader br = null;
    BufferedWriter bw = null;

    p = new FileReader(this.filePath);
    br = new BufferedReader(p);
    String context = "";
    String line = null;
    int count = 0;
    while ((line = br.readLine()) != null) {
      if (line.startsWith(properties + "=")) {
        String[] infos = line.split("=");
        context = context + infos[0] + "=" + value + "\r\n";
        count++;
      } else {
        context = context + line + "\r\n";
      }
    }
    if (count == 0) {
      context = context + properties + "=" + value;
    }
    br.close();
    p.close();
    bw = new BufferedWriter(new FileWriter(this.filePath));
    bw.write(context);
    bw.close();
  }

  public String getPropertiesValue(String prop, String defaultValue)
    throws Exception
  {
    try
    {
      String propValue = "";
      if (Util.stringIsEmpty(defaultValue)) {
        propValue = this.propFile.getProperty(prop).trim();
      }
      return this.propFile.getProperty(prop, defaultValue).trim();
    }
    catch (Exception e) {
    }
    return defaultValue;
  }

  public String getPropertiesValue(String prop) throws Exception
  {
    return getPropertiesValue(prop, null);
  }
}
