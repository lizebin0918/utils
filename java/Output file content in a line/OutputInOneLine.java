package com.lzb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class OutputInOneLine {

	public static void main(String[] args) {
		System.out.println(outputInOneLine("d://1.xml"));
	}
	
	public static String outputInOneLine(String filePath) {
		File file = new File(filePath);
		if(!file.exists()) return "文件不存在";
		if(!file.isFile()) return "只能解析文件内容";
		StringBuffer retString = new StringBuffer();
		String temp = "";
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((temp = br.readLine()) != null) {
				retString.append(temp);
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("意外异常。");
		} finally {
			if(br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					System.out.println("关闭流报错");
				}
			} 
			if(fr != null) {
				try {
					fr.close();
					fr = null;
				} catch(IOException e) {
					System.out.println("关闭流报错");
				}
			}
		}
		return retString.toString().replaceAll("[\r,\n,\t]", "");
	}
}
