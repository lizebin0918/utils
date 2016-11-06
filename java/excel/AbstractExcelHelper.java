package com.yaodian.helper.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel工具抽象类
 * 
 * @author liujiduo
 * 
 */
public abstract class AbstractExcelHelper {
	/**
	 * 对象序列化版本号名称
	 */
	public static final String UID = "12319456423134534545";

    public static final String FILE_SUFFIX_XLS = "xls";
    public static final String FILE_SUFFIX_XLSx = "xlsx";
	
	/**
	 * 将指定excel文件中的数据转换成数据列表
	 * 
	 * @param clazz
	 *            数据类型
	 * @param sheetNo
	 *            工作表编号
	 * @param dataLineIndex
	 *            数据行索引，从1开始
	 * @return 返回转换后的数据列表
	 * @throws Exception
	 */
	public <T> List<T> readExcel(Class<T> clazz, int sheetNo, int dataLineIndex)
			throws Exception {
		Field[] fields = clazz.getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			fieldNames[i] = fieldName;
		}
		return readExcel(clazz, fieldNames, sheetNo, dataLineIndex);
	}

	/**
	 * 将指定excel文件中的数据转换成数据列表
	 * 
	 * @param clazz
	 *            数据类型
	 * @param fieldNames
	 *            属性列表
	 * @param dataLineIndex
	 *            数据行索引，从1开始
	 * @return 返回转换后的数据列表
	 * @throws Exception
	 */
	public <T> List<T> readExcel(Class<T> clazz, String[] fieldNames,
			int dataLineIndex) throws Exception {
		return readExcel(clazz, fieldNames, 0, dataLineIndex);
	}

	/**
	 * 抽象方法：将指定excel文件中的数据转换成数据列表，由子类实现
	 * 
	 * @param clazz
	 *            数据类型
	 * @param fieldNames
	 *            属性列表
	 * @param sheetNo
	 *            工作表编号，从0开始
	 * @param dataLineIndex
	 *            数据行索引，从1开始
	 * @return 返回转换后的数据列表
	 * @throws Exception
	 */
	public abstract <T> List<T> readExcel(Class<T> clazz, String[] fieldNames,
			int sheetNo, int dataLineIndex) throws Exception;

	/**
	 * 写入数据到指定excel文件中
	 * 
	 * @param clazz
	 *            数据类型
	 * @param dataModels
	 *            数据列表
	 * @param sheetNameOrSheetIndex
	 * 			  sheet名字
	 * @param isCopyHeaderFormat
	 * 			  是否拷贝表头格式
	 * @throws Exception
	 */
	public <T> void writeExcel(Class<T> clazz, List<T> dataModels, String sheetNameOrSheetIndex, boolean isCopyHeaderFormat)
			throws Exception {
		Field[] fields = clazz.getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			fieldNames[i] = fieldName;
		}
		writeExcel(clazz, dataModels, fieldNames, fieldNames, sheetNameOrSheetIndex, isCopyHeaderFormat);
	}

	/**
	 * 写入数据到指定excel文件中
	 * 
	 * @param clazz
	 *            数据类型
	 * @param dataModels
	 *            数据列表
	 * @param fieldNames
	 *            属性列表
	 * @param sheetNameOrSheetIndex
	 * 			  sheet名字或者索引
	 * @param isCopyHeaderFormat
	 * 			  是否拷贝表头格式
	 * @throws Exception
	 */
	public <T> void writeExcel(Class<T> clazz, List<T> dataModels,
			String[] fieldNames, String sheetNameOrSheetIndex, boolean isCopyHeaderFormat) throws Exception {
		writeExcel(clazz, dataModels, fieldNames, fieldNames, sheetNameOrSheetIndex, isCopyHeaderFormat);
	}

	/**
	 * 抽象方法：写入数据到指定excel文件中，由子类实现
	 * 如果沿用表头格式，titles = null, isCopyHeaderFormat = true
	 * 
	 * @param clazz
	 *            数据类型
	 * @param dataModels
	 *            数据列表
	 * @param fieldNames
	 *            属性列表
	 * @param titles
	 *            标题列表
	 * @param sheetNameOrSheetIndex
	 * 			  sheet名字或者索引 从0开始
	 * @param isCopyHeaderFormat
	 * 			  是否拷贝表头格式
	 * @throws Exception
	 */
	public abstract <T> void writeExcel(Class<T> clazz, List<T> dataModels,
			String[] fieldNames, String[] titles, String sheetNameOrSheetIndex, boolean isCopyHeaderFormat) throws Exception;

    /**
     * 如果是os = HttpServletResponse.getWriter();需要设置:
     * response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, "utf-8") + ".xlsx");
     * @param os 输出流
     * @param sheetNameOrSheetIndex sheet索引，从0开始
     * @param titles 表头
     * @param keys Map的键名称
     * @param rows 数据行
     * @throws Exception
     */
    public abstract void writeExcel(OutputStream os, String sheetNameOrSheetIndex, String[] titles, String[] keys, List<Map<String, Object>> rows) throws IOException;

	/**
	 * 判断属性是否为日期类型
	 * 
	 * @param clazz
	 *            数据类型
	 * @param fieldName
	 *            属性名
	 * @return 如果为日期类型返回true，否则返回false
	 */
	protected <T> boolean isDateType(Class<T> clazz, String fieldName) {
		boolean flag = false;
		try {
			Field field = clazz.getDeclaredField(fieldName);
			Class<?> typeObj = field.getType().getClass();
			flag = typeObj == Date.class;
		} catch (Exception e) {
			// 把异常吞掉直接返回false
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 根据类型将指定参数转换成对应的类型
	 * 
	 * @param value
	 *            指定参数
	 * @param type
	 *            指定类型
	 * @return 返回类型转换后的对象
	 */
	protected <T> Object parseValueWithType(String value, Class<?> type) {
		Object result = null;
		try { // 根据属性的类型将内容转换成对应的类型
			if (Boolean.TYPE == type) {
				result = Boolean.parseBoolean(value);
			} else if (Byte.TYPE == type) {
				result = Byte.parseByte(value);
			} else if (Short.TYPE == type) {
				result = Short.parseShort(value);
			} else if (Integer.TYPE == type) {
				result = Integer.parseInt(value);
			} else if (Long.TYPE == type) {
				result = Long.parseLong(value);
			} else if (Float.TYPE == type) {
				result = Float.parseFloat(value);
			} else if (Double.TYPE == type) {
				result = Double.parseDouble(value);
			} else {
				result = (Object) value;
			}
		} catch (Exception e) {
			// 把异常吞掉直接返回null
		}
		return result;
	}

    /*public static void main(String[] args) throws Exception {
        File sourceFile = new File("/Users/lizebin/Desktop/清远为百姓--药店通门店信息录入表0912.xls");
        String fileSuffix = sourceFile.getName().substring(sourceFile.getName().lastIndexOf(".") + 1, sourceFile.getName().length());
        //excel的表头名称位置对应，并且此字段属于类的属性
        String[] fieldNames = new String[] { "code", "name", "phone", "managerName",
                "managerPhone", "medicareFlag",
                "openTime", "nature", "type", "acreage", "zone",
                "provinceName", "cityName", "areaName", "street", "longitude", "latitude"};
        AbstractExcelHelper helperReader = null;
        if(FILE_SUFFIX_XLS.equalsIgnoreCase(fileSuffix)) {
            helperReader = HSSFExcelHelper.getInstance(sourceFile);
        } else if(FILE_SUFFIX_XLSx.equalsIgnoreCase(fileSuffix)) {
            helperReader = XSSFExcelHelper.getInstance(sourceFile);
        } else {
            System.out.println("未识别的文件类型[" + fileSuffix + "]");
            return;
        }
        List<CmsCustomerStoreExcel> rows = helperReader.readExcel(CmsCustomerStoreExcel.class, fieldNames, 3);
        System.out.println(rows.size());
        System.out.println(JSON.toJSONString(rows));
    }*/
}

