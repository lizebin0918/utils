package com.yaodian.helper.excel;

import com.yaodian.utils.reflection.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基于POI实现的Excel工具类--HSSF是POI工程对Excel 97(-2007)文件操作的纯Java实现 
 * 
 * @author liujiduo
 * 
 */
public class HSSFExcelHelper extends AbstractExcelHelper {

	public static final SimpleDateFormat format = new SimpleDateFormat();
	private volatile static HSSFExcelHelper instance = null; // 单例对象

	private File file; // 操作文件

	/**
	 * 私有化构造方法
	 */
	private HSSFExcelHelper() {
		super();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * 获取单例对象并进行初始化
	 * @param file
	 * @return 返回初始化后的单例对象
	 */
	public static HSSFExcelHelper getInstance(File file) {
		if (instance == null) {
			// 当单例对象为null时进入同步代码块
			synchronized (HSSFExcelHelper.class) {
				// 再次判断单例对象是否为null，防止多线程访问时多次生成对象
				if (instance == null) {
					instance = new HSSFExcelHelper();
				}
			}
		}
		if(file != null) {
            instance.setFile(file);
        }
		return instance;
	}

    /**
     * 获取单例对象并进行初始化
     *
     * @return 返回初始化后的单例对象
     */
    public static HSSFExcelHelper getInstance() {
        return getInstance(null);
    }

	@Override
	public <T> List<T> readExcel(Class<T> clazz, String[] fieldNames,
			int sheetNo, int dataLineIndex) throws Exception {
		List<T> dataModels = new ArrayList<T>();
		boolean isRowEmpty = false;
		// 获取excel工作簿
		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet = workbook.getSheetAt(sheetNo);
		
		int start = sheet.getFirstRowNum() + dataLineIndex - 1; // 如果有标题则从第二行开始
		
		for (int i = start; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			//begin:is empty row ?
			if (row == null) {
				sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
				i--;
				continue;
			}
			for (int j = 0; j < row.getLastCellNum(); j++) {
				if (row.getCell(j) == null || row.getCell(j).toString().trim().equals("")) {
					isRowEmpty = true;
				} else {
					isRowEmpty = false;
					break;
				}
			}
			if (isRowEmpty == true) {
				sheet.removeRow(row);
				i--;
				continue;
			}
			//after:is empty row ?
			// 生成实例并通过反射调用setter方法
			T target = clazz.newInstance();
			try {
				for (int j = 0; j < fieldNames.length; j++) {
					String fieldName = fieldNames[j];
					if (fieldName == null || UID.equals(fieldName)) {
						continue; // 过滤serialVersionUID属性
					}
					// 获取excel单元格的内容
					String content = "";
					HSSFCell cell = row.getCell(j);
					if (cell != null) {
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						content = cell.getStringCellValue();
					}
					// 如果属性是日期类型则将内容转换成日期对象
					if (isDateType(clazz, fieldName)) {
						// 如果属性是日期类型则将内容转换成日期对象
                        ReflectionUtils.invokeSetter(target, fieldName,
                                                     format.parse(content));
					} else {
						Field field = clazz.getDeclaredField(fieldName);
                        ReflectionUtils.invokeSetter(target, fieldName,
								parseValueWithType(content, field.getType()));
					}
				}
                dataModels.add(target);
			} catch(Exception e) {

			}
		}
		return dataModels;
	}

	@Override
	public <T> void writeExcel(Class<T> clazz, List<T> dataModels,
			String[] fieldNames, String[] titles, String sheetNameOrSheetIndex, boolean isCopyHeaderFormat) throws Exception {
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		// 检测文件是否存在，如果存在则修改文件，否则创建文件
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);
			workbook = new HSSFWorkbook(fis);
			if(sheetNameOrSheetIndex != null) {
				if(StringUtils.isNumeric(sheetNameOrSheetIndex)) {
					sheet = workbook.getSheetAt(Integer.parseInt(sheetNameOrSheetIndex));
				} else {
					sheet = workbook.getSheet(sheetNameOrSheetIndex);
				}
			} else {
				//getSheet(0) defaultly
				sheet = workbook.getSheetAt(0);
			}
		} else {
			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet(sheetNameOrSheetIndex);
		}
		Map<Integer, HSSFCellStyle> cellStyleMapping = new HashMap<>();
		// 添加表格标题
		if(titles != null) {//沿用原表头
			HSSFRow headRow = sheet.createRow(0);
			for (int i = 0; i < titles.length; i++) {
				HSSFCell cell = headRow.createCell(i);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(titles[i]);
				// 设置字体加粗
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				HSSFFont font = workbook.createFont();
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
				cellStyle.setFont(font);
				// 设置自动换行
				cellStyle.setWrapText(true);
				cell.setCellStyle(cellStyle);
				// 设置单元格宽度
				sheet.setColumnWidth(i, titles[i].length() * 1000);
			}
		} else {
			//获取表头格式
			if(fieldNames != null && isCopyHeaderFormat) {
				for(int i=0; i<fieldNames.length; i++) {
					HSSFCell _cell = sheet.getRow(0).getCell(i);
					//表头跟属性列表不一致将会错位
					if(_cell != null) {
						cellStyleMapping.put(i, _cell.getCellStyle());
					}
				}
			}
		}
		// 添加表格内容
		for (int i = 0; i < dataModels.size(); i++) {
			HSSFRow row = sheet.createRow(i + 1);
			// 遍历属性列表
			for (int j = 0; j < fieldNames.length; j++) {
				// 通过反射获取属性的值域
				String fieldName = fieldNames[j];
				if (fieldName == null || UID.equals(fieldName)) {
					continue; // 过滤serialVersionUID属性
				}
				Object result = ReflectionUtils.invokeGetter(dataModels.get(i),
						fieldName);
				HSSFCell cell = row.createCell(j);
				cell.setCellValue(result.toString());
				cell.setCellStyle(cellStyleMapping.get(j));
				// 如果是日期类型则进行格式化处理
				if (isDateType(clazz, fieldName)) {
					cell.setCellValue(format.format((Date) result));
				}
			}
		}
		// 将数据写到磁盘上
		FileOutputStream fos = new FileOutputStream(file);
		try {
			workbook.write(new FileOutputStream(file));
		} finally {
			if (fos != null) {
				fos.close(); // 不管是否有异常发生都关闭文件输出流
			}
		}
	}

    @Override
    public void writeExcel(OutputStream os, String sheetNameOrSheetIndex, String[] titles, String[] keys, List<Map<String, Object>> rows) throws IOException {
        HSSFWorkbook workbook = null;
        HSSFSheet sheet = null;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(sheetNameOrSheetIndex);
        if(titles != null && titles.length > 0) {
            HSSFRow headRow = sheet.createRow(0);
            for (int i = 0; i < titles.length; i++) {
                HSSFCell cell = headRow.createCell(i);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(titles[i]);
                // 设置字体加粗
                HSSFCellStyle cellStyle = workbook.createCellStyle();
                HSSFFont font = workbook.createFont();
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                cellStyle.setFont(font);
                // 设置自动换行
                cellStyle.setWrapText(true);
                cell.setCellStyle(cellStyle);
                // 设置单元格宽度
                sheet.setColumnWidth(i, titles[i].length() * 1000);
            }
        }
        // 添加表格内容
        for (int i = 0, size=rows.size(); i < size; i++) {
            Map<String, Object> _map = rows.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            for(int j=0, length=keys.length; j<length; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(String.valueOf(_map.get(keys[j])));
            }
        }
        workbook.write(os);
        os.flush();
    }
}
