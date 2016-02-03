package com.base.framework.common.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
	 * @return String
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
		
		DateFormat dateFormat = new SimpleDateFormat(format);
		try {
			Date formatDate = dateFormat.parse(date);
			return date.equals(dateFormat.format(formatDate));
		} catch (ParseException e) {
			return false;
		}
	}
	
	/**
     * 获取时间date1与date2相差的秒数
     * 
     * @param date1
     *            起始时间
     * @param date2
     *            结束时间
     * @return 返回相差的秒数
     */
    public static long getOffsetSeconds(Date date1, Date date2) {
        long seconds = (long) ((date2.getTime() - date1.getTime()) / 1000);
        return seconds;
    }
    
    /**
     * 获取时间date1与date2相差的分钟数
     * 
     * @param date1
     *            起始时间
     * @param date2
     *            结束时间
     * @return 返回相差的分钟数
     */
    public static long getOffsetMinutes(Date date1, Date date2) {
        return getOffsetSeconds(date1, date2) / 60;
    }
    
    /**
     * 获取时间date1与date2相差的小时数
     * 
     * @param date1
     *            起始时间
     * @param date2
     *            结束时间
     * @return 返回相差的小时数
     */
    public static long getOffsetHours(Date date1, Date date2) {
        return getOffsetMinutes(date1, date2) / 60;
    }
 
    /**
     * 获取时间date1与date2相差的天数数
     * 
     * @param date1
     *            起始时间
     * @param date2
     *            结束时间
     * @return 返回相差的天数
     */
    public static long getOffsetDays(Date date1, Date date2) {
        return getOffsetHours(date1, date2) / 24;
    }
    
    /**
     * 获取时间date1与date2相差的周数
     * 
     * @param date1
     *            起始时间
     * @param date2
     *            结束时间
     * @return 返回相差的周数
     */
    public static long getOffsetWeeks(Date date1, Date date2) {
        return getOffsetDays(date1, date2) / 7;
    }
    
    /**
     * 获取指定日期累加年月日后的时间
     * 
     * @param date
     *            指定日期
     * @param year
     *            指定年数
     * @param month
     *            指定月数
     * @param day
     *            指定天数
     * @return 返回累加年月日后的时间
     */
    public static Date rollDate(Date date, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.YEAR, year);
        cal.add(Calendar.MONTH, month);
        cal.add(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }
    
    /**
     * 获取指定日期累加指定月数后的时间
     * 
     * @param date
     *            指定日期
     * @param month
     *            指定月数
     * @return 返回累加月数后的时间
     */
    public static Date rollMonth(Date date, int month) {
        return rollDate(date, 0, month, 0);
    }
 
    /**
     * 获取指定日期累加指定天数后的时间
     * 
     * @param date
     *            指定日期
     * @param day
     *            指定天数
     * @return 返回累加天数后的时间
     */
    public static Date rollDay(Date date, int day) {
        return rollDate(date, 0, 0, day);
    }
 
    /**
     * 计算指定日期所在月份的天数
     * 
     * @param date
     *            指定日期
     * @return 返回所在月份的天数
     */
    public static int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        int dayOfMonth = cal.getActualMaximum(Calendar.DATE);
        return dayOfMonth;
    }

}
