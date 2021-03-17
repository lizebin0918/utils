package com.xk.common.utils.check;

public final class CheckTypeConst {

    private CheckTypeConst() {
    }

    /**
     * x 字符集:
     * 根据ASCII编码集依序标识字符是否为x字符集
     *
     * X 字符集: <br>
     * a b c d e f g h i j k l m n o p q r s t u v w x y z<br>
     * A B C D E F G H I J K L M N O P Q R S T U V W X Y Z<br>
     * 0 1 2 3 4 5 6 7 8 9 <br> . , - _ ( ) / = + : ? ! % * ; @ #<br>
     * (cr) (lf) (space)<br>
     */
    public static String CHECK_VALUE_TYPE_XSTRING = "XString";

    /**
     * Number [0-9]
     */
    public static String CHECK_VALUE_TYPE_NSTRING = "NString";

    /**
     * 由字母[a-z]或[A-Z]组成的字符串
     */
    public static String CHECK_VALUE_TYPE_ASTRING = "AString";

    /**
     * 由数字[0-9]和字母[a-z]或[A-Z]组成的字符串
     */
    public static String CHECK_VALUE_TYPE_ANSTRING = "ANString";

    /**
     * 由数字[0-9]或特殊字符组成的字符串
     */
    public static String CHECK_VALUE_TYPE_NSSTRING = "NSString";

    /**
     * 由数字[0-9]、字母[a-z]或[A-Z]和特殊字符组成的字符串
     */
    public static String CHECK_VALUE_TYPE_ANSSTRING = "ANSString";

    /**
     * X字符集 + GBK字符集组成的字符串
     */
    public static String CHECK_VALUE_TYPE_GBSTRING = "GBString";

    /**
     * 十六进制字符：[0123456789abcdefABCDEF]
     */
    public static String CHECK_VALUE_TYPE_HEXBINARY = "HexBinary";

    /**
     * 时间格式校验:HH:mm:ss,注意:23:9:9属于不合法
     */
    public static String CHECK_VALUE_TYPE_TIME = "Time";

    /**
     * 日期格式校验:yyyy-MM-dd
     */
    public static String CHECK_VALUE_TYPE_DATE = "Date";

    /**
     * 日期格式校验:yyyy-MM-dd HH:mm:ss
     */
    public static String CHECK_VALUE_TYPE_DATE_TIME = "Datetime";

}
