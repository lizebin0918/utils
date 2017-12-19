
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public final class CheckUtils {

    private CheckUtils() {
    }

    /**
     * HH:mm:ss
     */
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");
    /**
     * yyyy-MM-dd
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("^((((1[6-9]|[2-9]\\d)\\d{2})-(1[02]|0[13578])-([12]\\d|3[01]|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-(1[012]|0[13456789])-([12]\\d|30|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-02-(1\\d|2[0-8]|0[1-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-02-29))$");
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private static final Pattern DATETIME_PATTERN = Pattern.compile("^((((1[6-9]|[2-9]\\d)\\d{2})-(1[02]|0[13578])-([12]\\d|3[01]|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-(1[012]|0[13456789])-([12]\\d|30|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-02-(1\\d|2[0-8]|0[1-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-02-29)) ([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");
    /**
     * 金额校验，最多只能包含两位小数，包含零元
     */
    private static final Pattern MONEY_PATTERN = Pattern.compile("^(([0-9]\\d*)(\\.\\d{1,2})?)$|^(0\\.0([1-9]?))$|^(0\\.([1-9]\\d?))$");

    /**
     * 检查字符串“value”是否由数字[0~9]组成的字符串
     *
     * @param value
     * @return
     */
    public static boolean isNString(String value) {
        if (value == null || value.trim().length() == 0) {
            return false;
        }
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!CharUtils.isAsciiNumeric(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串“value”是否由字母[a-z]或[A-Z]组成的字符串
     *
     * @param value
     * @return
     */
    public static boolean isAString(String value) {
        if (value == null || value.trim().length() == 0) {
            return false;
        }
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!CharUtils.isAsciiAlpha(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串“value”是否由数字[0-9]和字母[a-z]或[A-Z]组成的字符串
     *
     * @param value
     * @return
     */
    public static boolean isANString(String value) {
        if (value == null || value.trim().length() == 0) {
            return false;
        }
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!CharUtils.isAsciiAlphanumeric(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 时间格式:HH:mm:ss,注意:23:9:9属于不合法
     *
     * @param value
     * @return
     */
    public static boolean isTime(String value) {
        return TIME_PATTERN.matcher(value).find();
    }

    /**
     * 日期格式:yyyy-MM-dd
     *
     * @param value
     * @return
     */
    public static boolean isDate(String value) {
        return DATE_PATTERN.matcher(value).find();
    }

    /**
     * 日期时间格式:yyyy-MM-dd HH:mm:ss
     *
     * @param value
     * @return
     */
    public static boolean isDatetime(String value) {
        return DATETIME_PATTERN.matcher(value).find();
    }

    /**
     * 金额校验，最多只能包含两位小数
     * @param value
     * @return
     */
    public static boolean isMoneyIncludeZero(String value) {
        return MONEY_PATTERN.matcher(value).find();
    }

    /**
     * 判断字符是否为空格
     * @param c
     * @return
     */
    public static boolean isWhitespace(char c) {
        return Character.isWhitespace(c) || (c <= 0x000d && c >= 0x0009)
            || (c >= 0x0080 &&
            (c == 0x00a0 || c == 0x0085 || c == 0x1680 || c == 0x180e || (c >= 0x2000 && c <= 0x200a) ||
                c == 0x2028 || c == 0x2029 || c == 0x202f || c == 0x205f || c == 0x3000));
    }
}