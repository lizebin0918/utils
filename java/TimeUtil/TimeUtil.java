import java.text.DateFormat;
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
 * 	updated by lizebin on 2016-02-18
 * @version 1.0
 */

ppublic final class TimeUtil {

	private TimeUtil() {
		throw new Error("Don't instance" + TimeUtil.class.getName());
	}

	/**
	 * 格式为:yyyyMMddHHmmss
	 */
	public static final String DATE_FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";
	/**
	 * 格式为:yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATE_FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 格式为:HHmmss
	 */
	public static final String DATE_FORMAT_HHmmss = "HHmmss";
	/**
	 * 格式为:HH:mm:ss
	 */
	public static final String DATE_FORMAT_HH_mm_ss = "HH:mm:ss";
	/**
	 * 格式为:yyyyMMdd
	 */
	public static final String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
	/**
	 * 格式为:yyyy-MM-dd
	 */
	public static final String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
	/**
	 * 格式为:yyyy-MM-dd HH:mm
	 */
	public static final String DATE_FORMAT_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";

	private static final ThreadLocal<Map<String, InnerSimpleDateFormat>> innerVariables = new ThreadLocal<Map<String, InnerSimpleDateFormat>>();
	
	private static InnerSimpleDateFormat getSimpleDateFormatInstance(final String pattern) {
		Map<String, InnerSimpleDateFormat> sdfMap = innerVariables.get();
		if(sdfMap == null) {
			sdfMap = new HashMap<String, InnerSimpleDateFormat>(2);
			innerVariables.set(sdfMap);
		}
		InnerSimpleDateFormat sdf = sdfMap.get(pattern);
		if (sdf == null) {
			sdf = new InnerSimpleDateFormat(pattern);
			sdfMap.put(pattern, sdf);
		}
		return sdf;
	}
	
	private static class InnerSimpleDateFormat extends SimpleDateFormat {
		
		private static final long serialVersionUID = 1L;

		public InnerSimpleDateFormat(String pattern) {
			super(pattern);
		}
		
		@Deprecated
		public void applyPattern(String pattern) {
			throw new RuntimeException("date format don't chanage");
		}
	}

	/**
	 * 返回当月月份前n个月份的年月
	 * 
	 * @param today
	 * @param n
	 * @return
	 */
	public static String getPreMonthReturnYearMonth(String today,
			String format, int n) {
		String result = null;
		try {
			String tmp = changeStrTimeFormat(today, format,
					DATE_FORMAT_yyyyMMdd);
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			Calendar calendar = Calendar.getInstance();
			month = month - n - 1;
			if (month < 0) {
				year = year - 1;
				month = month + 12;
			}
			calendar.set(year, month, 1, 0, 0, 0);
			InnerSimpleDateFormat sdf = getSimpleDateFormatInstance("yyyyMM");
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
	public static String getLastDateOfTheMonth(String today,
			String inputFormat, String outputFormat) {
		String result = null;
		try {
			String tmp = changeStrTimeFormat(today, inputFormat,
					DATE_FORMAT_yyyyMMdd);
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			if (month == 12) {
				year = year + 1;
				month = 0;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, 1, 0, 0, 0);
			InnerSimpleDateFormat sdf = getSimpleDateFormatInstance(outputFormat);
			result = sdf.format(new Date(calendar.getTime().getTime() - 1000));
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
	public static String getPreMonthLastDate(String today, String inputFormat,
			String outputFormat) {
		String result = null;
		try {
			String tmp = changeStrTimeFormat(today, inputFormat,
					DATE_FORMAT_yyyyMMdd);
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month - 1, 1, 0, 0, 0);
			InnerSimpleDateFormat sdf = getSimpleDateFormatInstance(outputFormat);
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
	public static String getNextMonthFirstDate(String today,
			String inputFormat, String outputFormat) {
		String result = null;
		try {
			String tmp = changeStrTimeFormat(today, inputFormat,
					DATE_FORMAT_yyyyMMdd);
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			if (month == 12) {
				year = year + 1;
				month = 0;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, 1, 0, 0, 0);
			InnerSimpleDateFormat sdf = getSimpleDateFormatInstance(outputFormat);
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
	public static String getFirstDateOfTheMonth(String today,
			String inputFormat, String outputFormat) {
		String result = null;
		try {
			String tmp = changeStrTimeFormat(today, inputFormat,
					DATE_FORMAT_yyyyMMdd);
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month - 1, 1, 0, 0, 0);
			InnerSimpleDateFormat sdf = getSimpleDateFormatInstance(outputFormat);
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
			if (date == null)
				result = "";
			else {
				SimpleDateFormat sdf = getSimpleDateFormatInstance(format);
				result = sdf.format(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取当周第一天，周一作为一个星期的第一天
	 * @param today
	 * @param inputDateFormat
	 * @param outputDateFormat
	 * @return
	 */
	public static String getFirstDayOfWeek(String today, String inputDateFormat, String outputDateFormat) {
		Date sourceDate = changeStrToDate(today, inputDateFormat);
		Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(sourceDate);
        calendar.set(Calendar.DAY_OF_WEEK,
                      calendar.getFirstDayOfWeek());
        return dateFormat(calendar.getTime(), outputDateFormat);
	}
	
	/**
	 * 获取当周最后一天，周日为一个星期的最后一天
	 * @param today
	 * @param inputDateFormat
	 * @param outputDateFormat
	 * @return
	 */
	public static String getLastDayOfWeek(String today, String inputDateFormat, String outputDateFormat) {
		Date sourceDate = changeStrToDate(today, inputDateFormat);
		Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(sourceDate);
        calendar.set(Calendar.DAY_OF_WEEK,
                      calendar.getFirstDayOfWeek() + 6);
        return dateFormat(calendar.getTime(), outputDateFormat);
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
			InnerSimpleDateFormat sdf = getSimpleDateFormatInstance(format);
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
				InnerSimpleDateFormat sdf = getSimpleDateFormatInstance(oldFormat);
				Date tmp = sdf.parse(date);
				sdf = getSimpleDateFormatInstance(newFormat);
				result = sdf.format(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result == null) {
			return "";
		}
		return result;
	}

	/**
	 * 得到当前日期
	 **/
	public static String getCurDate(String dateFormat) {
		InnerSimpleDateFormat sdf = getSimpleDateFormatInstance(dateFormat);
		Calendar c1 = Calendar.getInstance(); // today
		return sdf.format(c1.getTime());
	}

	/**
	 * 计算从date开始n天以前（以后）的日期
	 * 
	 * @param date
	 * @param dateCnt
	 * @return
	 */
	public static Date getDateRelateToDate(Date date, int dateCnt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, dateCnt);
		return calendar.getTime();
	}

	/**
	 * 将字符串转换为日期类型
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date changeStrToDate(String date, String format) {
		InnerSimpleDateFormat sf = getSimpleDateFormatInstance(format);
		Date dt = null;
		try {
			dt = sf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dt;
	}

	/**
	 * 检查字符串是否给定的日期格式
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static boolean checkDate(String date, String format) {
		if (null == date || "".equals(date.trim())) {
			return false;
		}
		try {
			InnerSimpleDateFormat dateFormat = getSimpleDateFormatInstance(format);
			Date formatDate = dateFormat.parse(date);
			return changeStrTimeFormat(date, format, format).equals(dateFormat.format(formatDate));
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