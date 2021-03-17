package com.yaodian.utils.time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * 要求 JDK版本 >= 1.8
 * @author lizebin
 * 
 */
public final class TimeUtils {

	private TimeUtils() {
		throw new Error("Don't instance" + TimeUtils.class.getName());
	}

	public enum DATE_FORMAT {
        /**
         * 格式为:yyyyMMddHHmmssSSS
         */
        yyyyMMddHHmmssSSS("yyyyMMddHHmmssSSS"),
        /**
         * 格式为:yyyy-MM-dd HH:mm:ss.SSS
         */
        yyyy_MM_dd_HH_mm_ss_SSS("yyyy-MM-dd HH:mm:ss.SSS"),
        /**
         * 格式为:yyyyMMddHHmmss
         */
        yyyyMMddHHmmss("yyyyMMddHHmmss"),
        /**
         * 格式为:yyyy-MM-dd HH:mm:ss
         */
        yyyy_MM_dd_HH_mm_ss("yyyy-MM-dd HH:mm:ss"),
        /**
         * 格式为:HHmmss
         */
        HHmmss("HHmmss"),
        /**
         * 格式为:HH:mm:ss
         */
        HH_mm_ss("HH:mm:ss"),
        /**
         * 格式为:yyyyMMdd
         */
        yyyyMMdd("yyyyMMdd"),
        /**
         * 格式为:yyyy-MM-dd
         */
        yyyy_MM_dd("yyyy-MM-dd"),
        /**
         * 格式为:yyyy-MM-dd HH:mm
         */
        yyyy_MM_dd_HH_mm("yyyy-MM-dd HH:mm"),
        /**
         * 格式为:yyyyMMddHHmm
         */
        yyyyMMddHHmm("yyyyMMddHHmm"),
        /**
         * 格式为:yyyy-MM-dd HH
         */
        yyyy_MM_dd_HH("yyyy-MM-dd HH"),
        /**
         * 格式为:yyyyMMddHH
         */
        yyyyMMddHH("yyyyMMddHH"),
        /**
         * 格式为:yyyy-MM
         */
        yyyy_MM("yyyy-MM"),
        /**
         * 格式为:yyyyMM
         */
        yyyyMM("yyyyMM"),
        /**
         * 格式为:HH:mm
         */
        HH_mm("HH:mm"),
        /**
         * 格式为:HHmm
         */
        HHmm("HHmm"),
        /**
         * 格式为:MM:dd
         */
        MM_dd("MM:dd"),
        /**
         * 格式为:MMdd
         */
        MMdd("MMdd"),
        /**
         * 年份
         */
        yyyy("yyyy"),
        /**
         * 月份
         */
        MM("MM"),
        dd("dd"),
        HH("HH"),
        mm("mm"),
        ss("ss");

        private String format;
        DATE_FORMAT(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

    }

    /*线程变量:{"dateFormat":DateFormat}*/
	private static final ThreadLocal<EnumMap<DATE_FORMAT, SimpleDateFormat>> innerVariables = new ThreadLocal<>();
	
	private static SimpleDateFormat getDateFormatInstance(final DATE_FORMAT format) {
		EnumMap<DATE_FORMAT, SimpleDateFormat> sdfMap = innerVariables.get();
		if(sdfMap == null) {
			sdfMap = new EnumMap<>(DATE_FORMAT.class);
			innerVariables.set(sdfMap);
		}
        SimpleDateFormat sdf = sdfMap.get(format);
        String pattern = format.getFormat();
		if (sdf == null) {
			sdf = new SimpleDateFormat(pattern) {
                {
                    super.applyPattern(pattern);
                }
                @Override
                public void applyPattern(String pattern) {}//sdfMap存储了{"日期格式":DateFormat},所以DateFormat.applyPattern()不可用
            };
			sdfMap.put(format, sdf);
		}
		return sdf;
	}

    /**
     * 最小的年月日-1970-01-01 00:00:00(Date d = new Date(0L))
     * UTC：格林威治时间1970年01月01日00时00分00秒（UTC+8北京时间1970年01月01日08时00分00秒）
     */
    private static final LocalDateTime MIN_LOCAL_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0);
	
	/**
	 * 返回当月月份前n个月份的年月
	 * 
	 * @param today
	 * @param n
	 * @return
	 */
	public static String getPreMonthReturnYearMonth(String today, DATE_FORMAT format, int n) {
		String result = null;
        String tmp = changeStrTimeFormat(today, format, DATE_FORMAT.yyyyMMdd);
        int year = Integer.parseInt(tmp.substring(0, 4));
        int month = Integer.parseInt(tmp.substring(4, 6));
        Calendar calendar = Calendar.getInstance();
        month = month - n - 1;
        if (month < 0) {
            year = year - 1;
            month = month + 12;
        }
        calendar.set(year, month, 1, 0, 0, 0);
        SimpleDateFormat sdf = getDateFormatInstance(DATE_FORMAT.yyyyMM);
        result = sdf.format(new Date(calendar.getTime().getTime()));
		return result;
	}

	/**
	 * 返回当月最后一天日期
	 * 
	 * @param today
	 * @return
	 */
	public static String getLastDateOfMonth(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
		String result = null;
		try {
			String tmp = changeStrTimeFormat(today, inputFormat,
                                             DATE_FORMAT.yyyyMMdd);
			int year = Integer.parseInt(tmp.substring(0, 4));
			int month = Integer.parseInt(tmp.substring(4, 6));
			if (month == 12) {
				year = year + 1;
				month = 0;
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, 1, 0, 0, 0);
            SimpleDateFormat sdf = getDateFormatInstance(outputFormat);
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
	public static String getPreMonthLastDate(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        String tmp = changeStrTimeFormat(today, inputFormat, DATE_FORMAT.yyyyMMdd);
        int year = Integer.parseInt(tmp.substring(0, 4));
        int month = Integer.parseInt(tmp.substring(4, 6));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 0);
        SimpleDateFormat sdf = getDateFormatInstance(outputFormat);
        return sdf.format(new Date(calendar.getTime().getTime()));
	}

	/**
	 * 返回下一个月第一天日期
	 * 
	 * @param today
	 * @return
	 */
	public static String getNextMonthFirstDate(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        String tmp = changeStrTimeFormat(today, inputFormat, DATE_FORMAT.yyyyMMdd);
        int year = Integer.parseInt(tmp.substring(0, 4));
        int month = Integer.parseInt(tmp.substring(4, 6));
        if (month == 12) {
            year = year + 1;
            month = 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return getDateFormatInstance(outputFormat).format(calendar.getTime());
	}

	/**
	 * 返回本一个月第一天日期
	 * 
	 * @param today
	 * @return
	 */
	public static String getFirstDateOfMonth(String today,
                                             DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        String tmp = DATE_FORMAT.yyyyMMdd.equals(inputFormat) ? today : changeStrTimeFormat(today, inputFormat, DATE_FORMAT.yyyyMMdd);
        int year = Integer.parseInt(tmp.substring(0, 4));
        int month = Integer.parseInt(tmp.substring(4, 6));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0);
        SimpleDateFormat sdf = getDateFormatInstance(outputFormat);
        return sdf.format(calendar.getTime());
	}

	/**
	 * 格式日期转换
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateFormat(Date date, DATE_FORMAT format) {
        return getDateFormatInstance(format).format(date);
	}

	/**
	 * 字符串日期格式转换
	 * 
	 * @param date
	 * @param oldFormat
	 * @param newFormat
	 * @return
	 */
	public static String changeStrTimeFormat(String date, DATE_FORMAT oldFormat, DATE_FORMAT newFormat) {
        return getDateFormatInstance(newFormat).format(strTimeToDate(date, oldFormat));
	}

	/**
	 * 得到当前日期
	 **/
	public static String getCurDate(DATE_FORMAT dateFormat) {
		return getDateFormatInstance(dateFormat).format(new Date());
	}
	
	/**
	 * 获取当周第一天，周一作为一个星期的第一天
	 * @param today
	 * @param inputDateFormat
	 * @param outputDateFormat
	 * @return
	 */
	public static String getFirstDateOfWeek(String today, DATE_FORMAT inputDateFormat, DATE_FORMAT outputDateFormat) {
		Date sourceDate = strTimeToDate(today, inputDateFormat);
		Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(sourceDate);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return dateFormat(calendar.getTime(), outputDateFormat);
	}
	
	/**
	 * 获取当周最后一天，周日为一个星期的最后一天
	 * @param today
	 * @param inputDateFormat
	 * @param outputDateFormat
	 * @return
	 */
	public static String getLastDateOfWeek(String today, DATE_FORMAT inputDateFormat, DATE_FORMAT outputDateFormat) {
        Date sourceDate = strTimeToDate(today, inputDateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
        return dateFormat(new Date(calendar.getTime().getTime()), outputDateFormat);
	}
	
	/**
	 * 将字符串转换为日期类型
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date strTimeToDate(String date, DATE_FORMAT format) {
        try {
            return getDateFormatInstance(format).parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
		long seconds = ((date2.getTime() - date1.getTime()) / 1000);
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
	 * 获取时间startDate与endDate相差的天数数(endDate - startDate)
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
	 * 获取时间startDate与endDate相差的月数
	 * 
	 * @param date1
	 *            起始时间
	 * @param date2
	 *            结束时间
	 * @return 返回相差的天数
	 */
	public static int getOffsetMonths(Date date1, Date date2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int curYear = calendar.get(Calendar.YEAR);
		int curMonths =  calendar.get(Calendar.MONTH) + 1;
		int curDays = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.setTime(date2);
		int endYear = calendar.get(Calendar.YEAR);
		int endMonths =  calendar.get(Calendar.MONTH) + 1;
		int endDays = calendar.get(Calendar.DAY_OF_MONTH);
		int uy = (endYear - curYear) * 12 + (endMonths - curMonths) + (endDays < curDays ? -1 : 0); 
		return uy;
	}
	
	/**
	 * 时间计算 +(-)N分钟
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date rollMinutes(Date date, int minute){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		Date result = new Date(calendar.getTimeInMillis());
		return result;
	}
	
	/**
	 * 时间计算 +(-)N小时
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date rollHours(Date date, int hour){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		Date result = new Date(calendar.getTimeInMillis());
		return result;
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
        cal.setTime(date);
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
	public static Date rollMonths(Date date, int month) {
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
	public static Date rollDays(Date date, int day) {
		return rollDate(date, 0, 0, day);
	}

	/**
	 * 计算指定日期所在月份的天数
	 * 
	 * @param date
	 *            指定日期
	 * @return 返回所在月份的天数
	 */
	public static int getDaysOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
		int dayOfMonth = cal.getActualMaximum(Calendar.DATE);
		return dayOfMonth;
	}

	/**
	 * 将时间置为一天中最早的一秒
	 * @param today
	 * @param inputDateFormat
	 * @param outputDateFormat
	 * @return
	 */
	public static String getFirstSecondOfDay(String today, DATE_FORMAT inputDateFormat, DATE_FORMAT outputDateFormat){
		Date now = strTimeToDate(today, inputDateFormat);
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return dateFormat(calendar.getTime(), outputDateFormat);
	}

	/**
	 * 将时间置为一天最晚的一秒
	 * @param today
	 * @param inputDateFormat
	 * @param outputDateFormat
	 * @return
	 */
	public static String getLastSecondOfDay(String today, DATE_FORMAT inputDateFormat, DATE_FORMAT outputDateFormat){
		Date now = strTimeToDate(today, inputDateFormat);
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return dateFormat(calendar.getTime(), outputDateFormat);
	}
	
	/**
	 * 根据日期返回周几，周日为7
	 * @param date
	 * @param inputDateFormat
	 * @return
	 */
	public static int getWeekday(String date, DATE_FORMAT inputDateFormat) {
		Calendar c = Calendar.getInstance();
		c.setTime((strTimeToDate(date, inputDateFormat)));
		int weekday = c.get(Calendar.DAY_OF_WEEK) - 1;
		if(weekday <= 0) {
			return 7;
		}
		return weekday;
	}

	/*===================补充 JDK1.8 特性===========================*/

    private static final EnumMap<DATE_FORMAT, DateTimeFormatter> DATE_FORMAT_MAPPING = new EnumMap<>(DATE_FORMAT.class);
    static {
        for (DATE_FORMAT _dateFormat : DATE_FORMAT.values()) {
            DATE_FORMAT_MAPPING.put(_dateFormat, DateTimeFormatter.ofPattern(_dateFormat.getFormat()));
        }
    }

    /**
     * 时间戳转化为时间<br/>
     * Created on : 2016-09-07 20:16
     * @author lizebin
     * @version V1.0.0
     * @param timeMillis
     * @return
     */
    public static LocalDateTime timeMillisToDate(long timeMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
    }

    /**
     * 获取时间startDatetime与endDatetime相差的偏移量(endDatetime - startDatetime)
     *
     * @param units
     *              {
     *              ChronoUnit.YEARS,
     *              ChronoUnit.MONTHS,
     *              ChronoUnit.WEEKS,
     *              ChronoUnit.DAYS,
     *              ChronoUnit.HOURS,
     *              ChronoUnit.MINUTES,
     *              ChronoUnit.SECONDS
     *              }
     * @param startDatetime 起始时间
     * @param endDatetime   结束时间
     * @return 返回相差的月数
     */
    public static long getOffset(ChronoUnit units, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return units.between(startDatetime, endDatetime);
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     * @author lizebin
     * @version V1.0.0
     * @param value
     * @return
     */
    public static long dateToTimeMillis(Date value) {
        return value.getTime();
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     * @author lizebin
     * @version V1.0.0
     * @param value
     * @return
     */
    public static long dateToTimeMillis(LocalDate value) {
        return value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     * @author lizebin
     * @version V1.0.0
     * @param value
     * @return
     */
    public static long dateToTimeMillis(LocalTime value) {
        return value.atDate(MIN_LOCAL_DATE_TIME.toLocalDate()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     * @author lizebin
     * @version V1.0.0
     * @param value
     * @return
     */
    public static long dateToTimeMillis(LocalDateTime value) {
        return value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 格式日期转换
     *
     * @param timeMillis 时间戳
     * @param dateFormat
     * @return
     */
    public static String dateFormat(long timeMillis, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(timeMillisToDate(timeMillis));
    }

    /**
     * 日期格式化<br/>
     * Created on : 2016-09-19 18:18
     * @author lizebin
     * @version V1.0.0
     * @param temporal
     * @param dateFormat
     * @return
     */
    public static String dateFormat(LocalDate temporal, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(temporal.atStartOfDay());
    }

    /**
     * 日期格式化<br/>
     * Created on : 2016-09-19 18:18
     * @author lizebin
     * @version V1.0.0
     * @param temporal
     * @param dateFormat
     * @return
     */
    public static String dateFormat(LocalTime temporal, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(temporal.atDate(MIN_LOCAL_DATE_TIME.toLocalDate()));
    }

    /**
     * 日期格式化<br/>
     * Created on : 2016-09-19 18:18
     * @author lizebin
     * @version V1.0.0
     * @param temporal
     * @param dateFormat
     * @return
     */
    public static String dateFormat(LocalDateTime temporal, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(temporal);
    }

    /**
     * 获取时间戳（毫秒为单位）<br/>
     * Created on : 2016-10-14 13:52
     * @author lizebin
     * @version V1.0.0
     * @return
     */
    public static long getTimeMillis() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取时间戳（秒为单位）<br/>
     * Created on : 2016-10-14 13:52
     * @author lizebin
     * @version V1.0.0
     * @return
     */
    public static long getTimeSecond() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * Date 转换成 LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}