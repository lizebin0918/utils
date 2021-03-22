import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;

/**
 * Required jdk1.8+
 *
 * @author lizebin
 *         <p>
 *         Notice:形参不再判断是否为空，也不判断格式是否正确，所有异常由外部处理
 */
public final class TimeUtils {

    private TimeUtils() {
        throw new Error("Don't instance" + TimeUtils.class.getName());
    }

    public enum DATE_FORMAT {
        yyyy_M_d("yyyy-M-d"),
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
        ss("ss"),
        yyMMdd("yyMMdd"),
        yyMMddHHmmss("yyMMddHHmmss");

        private final String format;

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
        if (sdfMap == null) {
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
                public void applyPattern(String pattern) {
                }//sdfMap存储了{"日期格式":DateFormat},所以DateFormat.applyPattern()不可用
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

    /*默认的 zoneId*/
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    /**
     * 返回当月最后一天日期
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getLastDateOfMonth(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        return dateFormat(dateToLocalDateTime(strTimeToDate(today, inputFormat)).with(TemporalAdjusters.lastDayOfMonth()), outputFormat);
    }

    /**
     * 返回当月最后一天日期
     *
     * @param now
     * @return
     */
    public static Date getLastDateOfMonth(Date now) {
        return temporalToDate(dateToLocalDateTime(now).with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * 返回上一个月最后一天日期
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getPreMonthLastDate(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        return dateFormat(dateToLocalDateTime(strTimeToDate(today, inputFormat)).minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()), outputFormat);
    }

    /**
     * 返回上一个月最后一天日期
     *
     * @param now
     * @return
     */
    public static Date getPreMonthLastDate(Date now) {
        return temporalToDate(dateToLocalDateTime(now).minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * 返回上一个月第一天日期
     *
     * @param now
     * @return
     */
    public static Date getPreMonthFirstDate(Date now) {
        return temporalToDate(dateToLocalDateTime(now).minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
    }

    /**
     * 返回下一个月第一天日期
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getNextMonthFirstDate(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        return dateFormat(dateToLocalDateTime(strTimeToDate(today, inputFormat)).plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()), outputFormat);
    }

    /**
     * 返回下一个月第一天日期
     *
     * @param now
     * @return
     */
    public static Date getNextMonthFirstDate(Date now) {
        return temporalToDate(dateToLocalDateTime(now).plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
    }

    /**
     * 返回本一个月第一天日期
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getFirstDateOfMonth(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        return dateFormat(dateToLocalDateTime(strTimeToDate(today, inputFormat)).with(TemporalAdjusters.firstDayOfMonth()), outputFormat);
    }

    /**
     * 返回本一个月第一天日期
     *
     * @param now
     * @return
     */
    public static Date getFirstDateOfMonth(Date now) {
        return temporalToDate(dateToLocalDateTime(now).with(TemporalAdjusters.firstDayOfMonth()));
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
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getFirstDateOfWeek(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        return dateFormat(dateToLocalDateTime(strTimeToDate(today, inputFormat)).with(DayOfWeek.MONDAY), outputFormat);
    }

    /**
     * 获取当周第一天，周一作为一个星期的第一天
     *
     * @param now
     * @return
     */
    public static Date getFirstDateOfWeek(Date now) {
        return temporalToDate(dateToLocalDateTime(now).with(DayOfWeek.MONDAY));
    }

    /**
     * 获取当周最后一天，周日为一个星期的最后一天
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getLastDateOfWeek(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        return dateFormat(dateToLocalDateTime(strTimeToDate(today, inputFormat)).with(DayOfWeek.SUNDAY), outputFormat);
    }

    /**
     * 获取当周最后一天，周日为一个星期的最后一天
     *
     * @param now
     * @return
     */
    public static Date getLastDateOfWeek(Date now) {
        return temporalToDate(dateToLocalDateTime(now).with(DayOfWeek.SUNDAY));
    }

    /**
     * 获取一年的开始时间
     *
     * @param now
     * @return
     */
    public static Date getStartTimeOfYear(Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);

        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * 获取本年开始时间
     *
     * @return
     */
    public static Date getStartTimeOfYear() {
        return getStartTimeOfYear(new Date());
    }

    /**
     * 获取一年的结束时间
     *
     * @param now
     * @return
     */
    public static Date getEndTimeOfYear(Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);

        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);

        return c.getTime();
    }

    /**
     * 获取本年结束时间
     *
     * @return
     */
    public static Date getEndTimeOfYear() {
        return getEndTimeOfYear(new Date());
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
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的秒数
     */
    public static long getOffsetSeconds(Date date1, Date date2) {
        return ((date2.getTime() - date1.getTime()) / 1000);
    }

    /**
     * 获取时间date1与date2相差的分钟数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的分钟数
     */
    public static long getOffsetMinutes(Date date1, Date date2) {
        return getOffsetSeconds(date1, date2) / 60;
    }

    /**
     * 获取时间date1与date2相差的小时数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的小时数
     */
    public static long getOffsetHours(Date date1, Date date2) {
        return getOffsetMinutes(date1, date2) / 60;
    }

    /**
     * 获取时间startDate与endDate相差的天数数(endDate - startDate)
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的天数
     */
    public static long getOffsetDays(Date date1, Date date2) {
        return getOffsetHours(date1, date2) / 24;
    }

    /**
     * 获取时间date1与date2相差的周数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的周数
     */
    public static long getOffsetWeeks(Date date1, Date date2) {
        return getOffsetDays(date1, date2) / 7;
    }

    /**
     * 获取时间startDate与endDate相差的月数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的天数
     */
    public static int getOffsetMonths(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int curYear = calendar.get(Calendar.YEAR);
        int curMonths = calendar.get(Calendar.MONTH) + 1;
        int curDays = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(date2);
        int endYear = calendar.get(Calendar.YEAR);
        int endMonths = calendar.get(Calendar.MONTH) + 1;
        int endDays = calendar.get(Calendar.DAY_OF_MONTH);
        return (endYear - curYear) * 12 + (endMonths - curMonths) + (endDays < curDays ? -1 : 0);
    }

    /**
     * 时间计算 +(-)N分钟
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date rollMinutes(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 时间计算 +(-)N小时
     *
     * @param date
     * @param hour
     * @return
     */
    public static Date rollHours(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 获取指定日期累加年月日后的时间
     *
     * @param date  指定日期
     * @param year  指定年数
     * @param month 指定月数
     * @param day   指定天数
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
     * @param date  指定日期
     * @param month 指定月数
     * @return 返回累加月数后的时间
     */
    public static Date rollMonths(Date date, int month) {
        return rollDate(date, 0, month, 0);
    }

    /**
     * 获取指定日期累加指定天数后的时间
     *
     * @param date 指定日期
     * @param day  指定天数
     * @return 返回累加天数后的时间
     */
    public static Date rollDays(Date date, int day) {
        return rollDate(date, 0, 0, day);
    }

    /**
     * 计算指定日期所在月份的天数
     *
     * @param date 指定日期
     * @return 返回所在月份的天数
     */
    public static int getDaysOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 将时间置为一天中最早的一秒
     *
     * @param date
     * @return
     */
    public static Date getFirstSecondOfDate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 将时间置为一天中最后一秒
     *
     * @param date
     * @return
     */
    public static Date getLastSecondOfDate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 将时间置为一天中最早的一秒
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getFirstSecondOfDay(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        Date now = strTimeToDate(today, inputFormat);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return dateFormat(calendar.getTime(), outputFormat);
    }

    /**
     * 将时间置为一天最晚的一秒
     *
     * @param today
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String getLastSecondOfDay(String today, DATE_FORMAT inputFormat, DATE_FORMAT outputFormat) {
        Date now = strTimeToDate(today, inputFormat);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return dateFormat(calendar.getTime(), outputFormat);
    }

    /**
     * 根据日期返回周几，周日为7
     *
     * @param date
     * @param inputFormat
     * @return
     */
    public static int getWeekday(String date, DATE_FORMAT inputFormat) {
        Calendar c = Calendar.getInstance();
        c.setTime((strTimeToDate(date, inputFormat)));
        int weekday = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekday <= 0) {
            return 7;
        }
        return weekday;
    }

    private static final EnumMap<DATE_FORMAT, DateTimeFormatter> DATE_FORMAT_MAPPING = new EnumMap<>(DATE_FORMAT.class);

    static {
        for (DATE_FORMAT _dateFormat : DATE_FORMAT.values()) {
            DATE_FORMAT_MAPPING.put(_dateFormat, DateTimeFormatter.ofPattern(_dateFormat.getFormat()));
        }
    }

    public static DateTimeFormatter getDateTimeFormatter(DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat);
    }

    /**
     * 时间戳转化为时间<br/>
     * Created on : 2016-09-07 20:16
     *
     * @param timeMillis
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static LocalDateTime timeMillisToLocalDateTime(long timeMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZONE_ID);
    }

    /**
     * 时间戳转化为时间<br/>
     * Created on : 2017-12-11 20:16
     *
     * @param timeMillis
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static Date timeMillisToDate(long timeMillis) {
        return new Date(timeMillis);
    }

    /**
     * 获取时间startDatetime与endDatetime相差的偏移量(endDatetime - startDatetime)
     *
     * @param units         {
     *                      ChronoUnit.YEARS,
     *                      ChronoUnit.MONTHS,
     *                      ChronoUnit.WEEKS,
     *                      ChronoUnit.DAYS,
     *                      ChronoUnit.HOURS,
     *                      ChronoUnit.MINUTES,
     *                      ChronoUnit.SECONDS
     *                      }
     * @param startDatetime 起始时间
     * @param endDatetime   结束时间
     * @return 返回相差的月数
     */
    public static long getOffset(ChronoUnit units, Temporal startDatetime, Temporal endDatetime) {
        return units.between(startDatetime, endDatetime);
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     *
     * @param value
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long dateToTimeMillis(Date value) {
        return value.getTime();
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     *
     * @param value
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long dateToTimeMillis(LocalDate value) {
        return value.atStartOfDay().atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 时间类型返回时间戳，精确到秒<br/>
     * Created on : 2016-09-18 15:43
     *
     * @param value
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long dateToTimeSecond(LocalDate value) {
        return value.atStartOfDay().atZone(ZONE_ID).toInstant().getEpochSecond();
    }

    /**
     * 时间类型返回时间戳，精确到秒<br/>
     * Created on : 2016-09-18 15:43
     *
     * @param value
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long dateToTimeSecond(Date value) {
        return value.getTime() / 1000;
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2019-07-25 18:31
     *
     * @param value
     * @return 精确到秒的时间戳
     * @author zgq7
     * @version V1.0.0
     */
    public static long dateToTimeSecond(LocalDateTime value) {
        return value.atZone(ZONE_ID).toEpochSecond();
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     *
     * @param value
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long dateToTimeMillis(LocalTime value) {
        return value.atDate(MIN_LOCAL_DATE_TIME.toLocalDate()).atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 时间类型返回时间戳<br/>
     * Created on : 2016-09-18 15:43
     *
     * @param value
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long dateToTimeMillis(LocalDateTime value) {
        return value.atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 格式日期转换
     *
     * @param timeMillis 时间戳(毫秒)
     * @param dateFormat
     * @return
     */
    public static String dateFormat(long timeMillis, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(timeMillisToLocalDateTime(timeMillis));
    }

    /**
     * 日期格式化<br/>
     * Created on : 2016-09-19 18:18
     *
     * @param temporal
     * @param dateFormat
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static String dateFormat(LocalDate temporal, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(temporal.atStartOfDay());
    }

    /**
     * 日期格式化<br/>
     * Created on : 2016-09-19 18:18
     *
     * @param temporal
     * @param dateFormat
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static String dateFormat(LocalTime temporal, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(temporal.atDate(MIN_LOCAL_DATE_TIME.toLocalDate()));
    }

    /**
     * 日期格式化<br/>
     * Created on : 2016-09-19 18:18
     *
     * @param temporal
     * @param dateFormat
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static String dateFormat(LocalDateTime temporal, DATE_FORMAT dateFormat) {
        return DATE_FORMAT_MAPPING.get(dateFormat).format(temporal);
    }

    /**
     * 获取时间戳（毫秒为单位）<br/>
     * Created on : 2016-10-14 13:52
     *
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long getTimeMillis() {
        return LocalDateTime.now().atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 获取时间戳（秒为单位）<br/>
     * Created on : 2016-10-14 13:52
     *
     * @return
     * @author lizebin
     * @version V1.0.0
     */
    public static long getTimeSecond() {
        return LocalDateTime.now().atZone(ZONE_ID).toInstant().getEpochSecond();
    }

    /**
     * Date 转换成 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID);
    }

    /**
     * LocalDate 转 Date
     *
     * @param localDate
     * @return
     */
    public static Date temporalToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZONE_ID).toInstant());
    }

    /**
     * LocalTime 转 Date;年-月-日为:1970-01-01
     *
     * @param localTime
     * @return
     */
    public static Date temporalToDate(LocalTime localTime) {
        return Date.from(localTime.atDate(MIN_LOCAL_DATE_TIME.toLocalDate()).atZone(ZONE_ID).toInstant());
    }

    /**
     * LocalDatetime 转 Date
     *
     * @param localDatetime
     * @return
     */
    public static Date temporalToDate(LocalDateTime localDatetime) {
        return Date.from(localDatetime.atZone(ZONE_ID).toInstant());
    }


    /**
     * 计算两个时间之间间隔分钟数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long minutesBetweenStartAndEndTime(Date startTime, Date endTime) {
        return (endTime.getTime() - startTime.getTime()) / 1000 / 60;
    }

    /**
     * 当前月减多少月
     * @param month
     * @param monthsToSubtract
     * @return java.lang.Integer
     * @author wh
     * @date 2020/10/15
     */
    public static Integer minusMonths(Integer month, long monthsToSubtract) {
        LocalDate current =  LocalDate.of(month / 100, month % 100, 1);
        LocalDate mom = current.minusMonths(monthsToSubtract);
        return Integer.valueOf(String.format("%4d%02d", mom.getYear(), mom.getMonthValue()));
    }

}