package com.talent.common.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: talent
 * </p>
 * 
 * @author lsj
 * @version 1.0
 */

public class TimeUtil {
	public TimeUtil() {
	}

	/**
	 * 返回当月月份前n个月份的年月
	 * 
	 * @param today
	 * @param n
	 * @return
	 */
	public static String getPreMonth(String today, String format, int n) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			String tmp = changeStrTimeFormat(today, format, "yyyyMMdd");
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			Calendar calendar = Calendar.getInstance();
			month = month - n - 1;
			if (month < 0) {
				year = year - 1;
				month = month + 12;
			}
			calendar.set(year, month, 1, 0, 0, 0);
			result = sdf.format(new Date(calendar.getTime().getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 返回当月最后一天日期
	 * 
	 * @param today
	 * @return
	 */
	public static String getMonthLastDate(String today, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String tmp = changeStrTimeFormat(today, format, "yyyyMMdd");
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			if (month == 12) {
				year = year + 1;
				month = 0;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, 1, 0, 0, 0);
			result = sdf.format(new Date(
					calendar.getTime().getTime() - 1000 * 60 * 60));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 返回上一个月最后一天日期
	 * 
	 * @param today
	 * @return
	 */
	public static String getPreMonthLastDate(String today, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String tmp = changeStrTimeFormat(today, format, "yyyyMMdd");
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month - 1, 1, 0, 0, 0);
			result = sdf.format(new Date(
					calendar.getTime().getTime() - 1000 * 60 * 60));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 返回下一个月第一天日期
	 * 
	 * @param today
	 * @return
	 */
	public static String getNextMonthFirstDate(String today, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String tmp = changeStrTimeFormat(today, format, "yyyyMMdd");
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			if (month == 12) {
				year = year + 1;
				month = 0;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, 1, 0, 0, 0);
			result = sdf.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 返回本一个月第一天日期
	 * 
	 * @param today
	 * @return
	 */
	public static String getFirstDateOfTheMonth(String today, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String tmp = changeStrTimeFormat(today, format, "yyyyMMdd");
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month - 1, 1, 0, 0, 0);
			result = sdf.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 格式日期转换
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateFormat(Date date, String format) {
		String result = null;
		try {
			if(date == null)
				result = "";
			else{
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 字符串日期转换成Date型日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date strTimeToDate(String date, String format) {
		Date result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			result = sdf.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 字符串日期格式转换
	 * 
	 * @param date
	 * @param oldFormat
	 * @param newFormat
	 * @return
	 */
	public static String changeStrTimeFormat(String date, String oldFormat,
			String newFormat) {
		String result = null;
		try {
			if (date == null || date.equals(""))
				return "";
			else {
				SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
				Date tmp = sdf.parse(date);
				sdf.applyPattern(newFormat);
				result = sdf.format(tmp);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		if (result == null) {
			return "";
		}
		return result;
	}
	/**
	 * 得到当前日期
	**/
	public static String getCurDate( String dateFormat ) 
	{
		java.text.SimpleDateFormat sdf = 
			new java.text.SimpleDateFormat(dateFormat);
		Calendar c1 = Calendar.getInstance(); // today
		return sdf.format(c1.getTime());
	}
	
	/**
	 * 计算从date开始n天以前（以后）的日期
	 * @param date
	 * @param dateCnt
	 * @return
	 */
	public static Date getDateRelateToDate(Date date, int dateCnt){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, dateCnt);
		return calendar.getTime();
	}
	
	/**
	 * 计算从date开始n月以前（以后）的日期
	 * @param date
	 * @param monthCnt
	 * @return
	 */
	public static Date getDateRelateToMonth(Date date, int monthCnt){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, monthCnt);
		return calendar.getTime();
	}
	
	/**
	 * 将字符串转换为日期类型
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date changeStrToDate(String date, String format){
		SimpleDateFormat sf = new SimpleDateFormat(format);
		Date dt = null;
		try {
			dt = sf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dt;
	}
	
	/**
	 * 取得上一个工作日
	 * @param date
	 * @return
	 */
	public static Date getLastWorkday(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		  int today = calendar.getTime().getDay();
		  if(today == calendar.getFirstDayOfWeek()){
			  calendar.roll(Calendar.DAY_OF_YEAR, -3);
		  }else{
			  calendar.roll(Calendar.DAY_OF_YEAR, -1);
		  }
		  return calendar.getTime();
	}
	
	/**
	 * 检查字符串是否给定的日期格式
	 * @param date
	 * @param format
	 * @return
	 */
	public static boolean checkDate(String date, String format) {
		if (null == format || null == date) {
			return false;
		}
		
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			df.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

}
