/**
 * 心康字符串工具类<br/>
 * Created on : 2017-12-18 17:10
 * @author lizebin
 */
public class XkStringUtils {

	/**
	 * 删除字符串空格
	 * @param str
	 * @return
	 */
	public static String trim(final String str) {
		if (Objects.isNull(str)) {
			return str;
		}
		int len = str.length();
		int st = 0;
		char[] val = str.toCharArray();

		while ((st < len) && isWhitespace(val[st])) {
			st++;
		}
		while ((st < len) && isWhitespace(val[len - 1])) {
			len--;
		}
		return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
	}

	/**
	 * 删除字符串空格
	 * @param str
	 * @return
	 */
	public static String deleteWhitespace(final String str) {
		if (Objects.isNull(str) || str.trim().length() == 0) {
			return str;
		}
		final int sz = str.length();
		final char[] chs = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++) {
			if (!isWhitespace(str.charAt(i))) {
				chs[count++] = str.charAt(i);
			}
		}
		if (count == sz) {
			return str;
		}
		return new String(chs, 0, count);
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
