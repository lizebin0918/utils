package com.kingkit.vote.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Excel导出工具
 * 
 * @version 1.0
 * @author 黄庆勇
 * @date 2010-05-10
 * 
 */
public class ExportToExcelUtil {
	private ExportToExcelUtil() {
	}

	/**
	 * 导出数据到多个工作表，并输出到response输出流下载
	 * 
	 * @param response
	 * @param filename
	 *            导出的Excel文件名
	 * @param sheetnames
	 *            工作表名列表
	 * @param titles
	 *            表头（以工作表为单位）
	 * @param contentList
	 *            导出的数据（以工作表为单位）
	 * @throws Exception
	 */
	public static void exportToExcel(HttpServletResponse response,
			String filename, List<String> sheetnames,
			List<List<String>> titles, List<List<String[]>> contentList)
			throws Exception {
		OutputStream out = response.getOutputStream(); // 取得输出流
		response.reset(); // 清空输出流
		response.setHeader("Content-disposition", "attachment; filename="
				+ filename + ".xls"); // 设定输出文件头
		response.setContentType("text/plain"); // 定义输出类型

		new ExportToExcelUtil().exportMainCode(out, sheetnames, titles,
				contentList);
	}

	/**
	 * 导出数据到单个工作表，并输出到response输出流下载
	 * 
	 * @param response
	 * @param filename
	 *            导出的Excel文件名
	 * @param sheetname
	 *            工作表名
	 * @param titles
	 *            表头
	 * @param contents
	 *            导出的数据
	 * @throws Exception
	 */
	public static void exportToExcel(HttpServletResponse response,
			String filename, String sheetname, List<String> titles,
			List<String[]> contents) throws Exception {
		List<String> sheetnameList = new ArrayList<String>();
		List<List<String>> titleList = new ArrayList<List<String>>();
		List<List<String[]>> contentList = new ArrayList<List<String[]>>();
		sheetnameList.add(sheetname);
		titleList.add(titles);
		contentList.add(contents);
		ExportToExcelUtil.exportToExcel(response, filename, sheetnameList,
				titleList, contentList);
	}

	/**
	 * 导出数据到多个工作表，输出到服务器
	 * 
	 * @param filename
	 *            导出的Excel文件名（在服务器中的绝对路径）
	 * @param sheetnames
	 *            工作表名列表
	 * @param titles
	 *            表头（以工作表为单位）
	 * @param contentList
	 *            导出的数据（以工作表为单位）
	 * @throws Exception
	 */
	public static void exportToExcel(String filename, List<String> sheetnames,
			List<List<String>> titles, List<List<String[]>> contentList)
			throws Exception {
		File file = new File(filename);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		OutputStream out = new FileOutputStream(file);

		new ExportToExcelUtil().exportMainCode(out, sheetnames, titles,
				contentList);
	}

	/**
	 * 导出数据到单个工作表，输出到服务器
	 * 
	 * @param filename
	 *            导出的Excel文件名（在服务器中的绝对路径）
	 * @param sheetname
	 *            工作表名
	 * @param titles
	 *            表头
	 * @param contents
	 *            导出的数据
	 * @throws Exception
	 */
	public static void exportToExcel(String filename, String sheetname,
			List<String> titles, List<String[]> contents) throws Exception {
		List<String> sheetnameList = new ArrayList<String>();
		List<List<String>> titleList = new ArrayList<List<String>>();
		List<List<String[]>> contentList = new ArrayList<List<String[]>>();
		sheetnameList.add(sheetname);
		titleList.add(titles);
		contentList.add(contents);
		ExportToExcelUtil.exportToExcel(filename, sheetnameList, titleList,
				contentList);
	}

	/**
	 * 导出数据到Excel表核心代码
	 * 
	 * @param out
	 *            导出的文件的输出流
	 * @param sheetnames
	 *            工作表名列表
	 * @param titles
	 *            表头（以工作表为单位）
	 * @param contentList
	 *            导出的数据（以工作表为单位）
	 * @throws Exception
	 */
	private void exportMainCode(OutputStream out, List<String> sheetnames,
			List<List<String>> titles, List<List<String[]>> contentList)
			throws Exception {
		WritableWorkbook workbook = null;
		try {
			// 创建新的Excel 工作簿
			workbook = Workbook.createWorkbook(out);

			int sizeOfPerSheet = 60000;// 每个工作表的最大记录数
			int numberOfSheets = 1;// 每个类型导出的工作表数
			int indexOfSheets = 0;// 工作表序号
			for (int i = 0; i < sheetnames.size(); i++) {
				String sheetname = sheetnames.get(i);
				List<String> title = titles.get(i);
				List<String[]> context = contentList.get(i);

				numberOfSheets = context.size() / sizeOfPerSheet
						+ (context.size() % sizeOfPerSheet == 0 ? 0 : 1);
				for (int s = 0; s < numberOfSheets; s++) {

					// 在Excel工作簿中建一工作表
					WritableSheet wsheet = workbook.createSheet(
							1 == numberOfSheets ? sheetname : sheetname + "_"
									+ (s + 1), indexOfSheets++); // 工作表名、工作表序号（从0开始）
					WritableFont font = new WritableFont(WritableFont.ARIAL,
							14, WritableFont.BOLD, false,
							UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
					WritableCellFormat format = new WritableCellFormat(font);

					for (int m = 0; m < title.size(); m++) {
						String title1 = title.get(m);
						Label wlabel1 = new Label(m, 0, title1, format); // 列、行、单元格中的文本、文本格式
						wsheet.addCell(wlabel1);
					}
					font = new WritableFont(WritableFont.createFont("宋体"), 12,
							WritableFont.NO_BOLD, false,
							UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
					format = new WritableCellFormat(font);

					for (int m = s * sizeOfPerSheet; m < (s + 1)
							* sizeOfPerSheet
							&& m < context.size(); m++) { // 在索引0的位置创建行（最顶端的行）
						String[] sdata = context.get(m);
						for (int j = 0; j < sdata.length; j++) { // 在索引0的位置创建单元格（左上端）
							Label wlabel1 = new Label(j,
									m % sizeOfPerSheet + 1, sdata[j], format); // 列、行、单元格中的文本、文本格式
							wsheet.addCell(wlabel1);
						}
					}

				}
			}
			workbook.write(); // 写入文件
		} catch (WriteException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (null != workbook) {
				workbook.close();
			}
			if (null != out) {
				out.close();
			}
		}
	}
}
