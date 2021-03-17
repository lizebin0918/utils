import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.regex.Pattern;

public final class CheckUtils {

    private CheckUtils() {
    }

    /**
     * 检查字符串“value”的类型是否为“valueType”。如类型“valueType”未定义，返回false
     *
     * @param value
     * @param valueType
     * @return
     */
    public static boolean checkValueType(String value, String valueType) {
        if (CheckTypeConst.CHECK_VALUE_TYPE_XSTRING.equals(valueType)) {
            // x 字符集
            return isXString(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_NSTRING
                .equals(valueType)) {
            // Number [0-9]
            return isXString(value) && isNString(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_ASTRING
                .equals(valueType)) {
            // 由字母[a-z]或[A-Z]组成的字符串
            return isXString(value) && isAString(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_ANSTRING
                .equals(valueType)) {
            // 由数字[0-9]和字母[a-z]或[A-Z]组成的字符串
            return isXString(value) && isANString(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_GBSTRING
                .equals(valueType)) {
            // X字符集 + GBK字符集组成的字符串
            return isGBString(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_HEXBINARY
                .equals(valueType)) {
            // 十六进制字符：[0123456789abcdefABCDEF]
            return isHexBinary(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_ANSSTRING.equals(valueType)) {
            //由数字[0-9]或字母[a-z]或[A-Z]或特殊字符组成的字符串
            return isXString(value) && isANSString(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_NSSTRING.equals(valueType)) {
            //由数字[0-9]或特殊字符组成的字符串
            return isXString(value) && isNSString(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_TIME.equals(valueType)) {
            //HH:mm:ss
            return isTime(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_DATE.equals(valueType)) {
            //yyyy-MM-dd
            return isDate(value);
        } else if (CheckTypeConst.CHECK_VALUE_TYPE_DATE_TIME.equals(valueType)) {
            //yyyy-MM-dd HH:mm:ss
            return isDatetime(value);
        }
        return false;// 找不到预定义类型，配置出错，验证失败
    }

    /**
     * 检查字符串“value”是否x字符集中的字符串
     *
     * @param value
     * @return
     */
    private static boolean isXString(String value) {
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!isXString(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串“value”是否由数字[0~9]或特殊字符组成的字符串
     *
     * @param value
     * @return
     */
    private static boolean isNSString(String value) {
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!isNSString(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串“value”是否由字母[a-z]或[A-Z]或数字[0~9]或特殊字符组成的字符串
     *
     * @param value
     * @return
     */
    private static boolean isANSString(String value) {
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!isXString(value.charAt(i))) {
                if (!Character.isLetter(value.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查字符串“value”是否由数字[0~9]组成的字符串
     *
     * @param value
     * @return
     */
    private static boolean isNString(String value) {
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!Character.isDigit(value.charAt(i))) {
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
    private static boolean isAString(String value) {
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!Character.isLetter(value.charAt(i))) {
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
    private static boolean isANString(String value) {
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!Character.isLetterOrDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串“value”是否由X字符集 + GBK字符集组成的字符串
     *
     * @param value
     * @return
     */
    private static boolean isGBString(String value) {
        CharsetEncoder gbkEncoder = Charset.forName("GBK").newEncoder();
        for (int i = 0, length = value.length(); i < length; i++) {
            if ((!isXString(value.charAt(i)) && !gbkEncoder.canEncode(value.charAt(i))) || isXStringAndSpecialChar(value.charAt(i))) {
                // 非x字符集或者且非GBK字符集，或是9个特殊字符
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串“value”是否由十六进制字符：[0123456789abcdefABCDEF]的字符串
     *
     * @param value
     * @return
     */
    private static boolean isHexBinary(String value) {
        return value.matches("[0-9a-fA-F]+");
    }

    /**
     * 检查字符‘value’是否x字符集中的字符
     *
     * @param value
     * @return
     */
    private static boolean isXString(char value) {
        if (value > 127 || value < 0) {
            return false;
        } else {
            return isXString[value];
        }
    }

    private static boolean isNSString(char value) {
        if (value > 127 || value < 0 || (value >= 65 && value <= 90) || (value >= 97 && value <= 122)) {
            return false;
        } else {
            return isXString[value];
        }
    }

    /**
     * 检查字符‘value’是否为九个系统报文保留字符：  %  &  '  "  <  >  以及 (cr) 和 (lf)
     *
     * @param value
     * @return
     */
    private static boolean isXStringAndSpecialChar(char value) {
        if (value == 10 || value == 13 || value == 34 || (value >= 37 && value <= 39) || value == 58 || value == 60 || value == 62) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 时间格式:HH:mm:ss,注意:23:9:9属于不合法
     *
     * @param value
     * @return
     */
    private static boolean isTime(String value) {
        return Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$").matcher(value).find();
    }

    /**
     * 日期格式:yyyy-MM-dd
     *
     * @param value
     * @return
     */
    private static boolean isDate(String value) {
        return Pattern.compile("^((((1[6-9]|[2-9]\\d)\\d{2})-(1[02]|0[13578])-([12]\\d|3[01]|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-(1[012]|0[13456789])-([12]\\d|30|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-02-(1\\d|2[0-8]|0[1-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-02-29))$").matcher(value).find();
    }

    /**
     * 日期时间格式:yyyy-MM-dd HH:mm:ss
     *
     * @param value
     * @return
     */
    private static boolean isDatetime(String value) {
        return Pattern.compile("^((((1[6-9]|[2-9]\\d)\\d{2})-(1[02]|0[13578])-([12]\\d|3[01]|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-(1[012]|0[13456789])-([12]\\d|30|0[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-02-(1\\d|2[0-8]|0[1-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-02-29)) ([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$").matcher(value).find();
    }

    /**
     * 根据ASCII编码集依序标识字符是否为x字符集
     * <p>
     * X 字符集: <br>
     * a b c d e f g h i j k l m n o p q r s t u v w x y z<br>
     * A B C D E F G H I J K L M N O P Q R S T U V W X Y Z<br>
     * 0 1 2 3 4 5 6 7 8 9 <br> . , - _ ( ) / = + : ? ! % * ; @ #<br>
     * (cr) (lf) (space)<br>
     */
    private static boolean[] isXString = {false, /* 0 */
            false, /* 1 */
            false, /* 2 */
            false, /* 3 */
            false, /* 4 */
            false, /* 5 */
            false, /* 6 */
            false, /* 7 */
            false, /* 8 */
            false, /* 9 */
            false, /* 换行 *//* 10 */
            false, /* 11 */
            false, /* 12 */
            false, /* 回车 *//* 13 */
            false, /* 14 */
            false, /* 15 */
            false, /* 16 */
            false, /* 17 */
            false, /* 18 */
            false, /* 19 */
            false, /* 20 */
            false, /* 21 */
            false, /* 22 */
            false, /* 23 */
            false, /* 24 */
            false, /* 25 */
            false, /* 26 */
            false, /* 27 */
            false, /* 28 */
            false, /* 29 */
            false, /* 30 */
            false, /* 31 */
            true, /* 空格 *//* 32 */
            true, /* ! *//* 33 */
            false, /* " *//* 34 */
            true, /* # *//* 35 */
            false, /* 36 */
            false, /* % *//* 37 */
            true, /* & *//* 38 */
            false, /* 39 */
            true, /* ( *//* 40 */
            true, /* ) *//* 41 */
            true, /* * *//* 42 */
            true, /* + *//* 43 */
            true, /* , *//* 44 */
            true, /* - *//* 45 */
            true, /* . *//* 46 */
            true, /* / *//* 47 */
            true, /* 0 *//* 48 */
            true, /* 1 *//* 49 */
            true, /* 2 *//* 50 */
            true, /* 3 *//* 51 */
            true, /* 4 *//* 52 */
            true, /* 5 *//* 53 */
            true, /* 6 *//* 54 */
            true, /* 7 *//* 55 */
            true, /* 8 *//* 56 */
            true, /* 9 *//* 57 */
            false, /* : *//* 58 */
            true, /* ; *//* 59 */
            true, /* < *//* 60 */
            true, /* = *//* 61 */
            true, /* > *//* 62 */
            true, /* ? *//* 63 */
            true, /* @ *//* 64 */
            true, /* A *//* 65 */
            true, /* B *//* 66 */
            true, /* C *//* 67 */
            true, /* D *//* 68 */
            true, /* E *//* 69 */
            true, /* F *//* 70 */
            true, /* G *//* 71 */
            true, /* H *//* 72 */
            true, /* I *//* 73 */
            true, /* J *//* 74 */
            true, /* K *//* 75 */
            true, /* L *//* 76 */
            true, /* M *//* 77 */
            true, /* N *//* 78 */
            true, /* O *//* 79 */
            true, /* P *//* 80 */
            true, /* Q *//* 81 */
            true, /* R *//* 82 */
            true, /* S *//* 83 */
            true, /* T *//* 84 */
            true, /* U *//* 85 */
            true, /* V *//* 86 */
            true, /* W *//* 87 */
            true, /* X *//* 88 */
            true, /* Y *//* 89 */
            true, /* Z *//* 90 */
            false, /* 91 */
            false, /* 92 */
            false, /* 93 */
            false, /* 94 */
            true, /* _ *//* 95 */
            false, /* ' *//* 96 */
            true, /* a *//* 97 */
            true, /* b *//* 98 */
            true, /* c *//* 99 */
            true, /* d *//* 100 */
            true, /* e *//* 101 */
            true, /* f *//* 102 */
            true, /* g *//* 103 */
            true, /* h *//* 104 */
            true, /* i *//* 105 */
            true, /* j *//* 106 */
            true, /* k *//* 107 */
            true, /* l *//* 108 */
            true, /* m *//* 109 */
            true, /* n *//* 110 */
            true, /* o *//* 111 */
            true, /* p *//* 112 */
            true, /* q *//* 113 */
            true, /* r *//* 114 */
            true, /* s *//* 115 */
            true, /* t *//* 116 */
            true, /* u *//* 117 */
            true, /* v *//* 118 */
            true, /* w *//* 119 */
            true, /* x *//* 120 */
            true, /* y *//* 121 */
            true, /* z *//* 122 */
            false, /* 123 */
            false, /* 124 */
            false, /* 125 */
            false, /* 126 */
            false /* 127 */
    };
}