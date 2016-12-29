/**
 * 需要依赖:
 * <dependency>
 * <groupId>commons-codec</groupId>
 * <artifactId>commons-codec</artifactId>
 * <version>1.10</version>
 * </dependency>
 */
public class MD5Utils {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private static final char[] SPECIAL_CHARS_ARRAY = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*_=+-/".toCharArray();

    private static final MessageDigest md5 = DigestUtils.getMd5Digest();

    public static String MD5(String value) {
		StringBuilder md5StrBuff = new StringBuilder();
        md5.reset();
		md5.update(value.getBytes(StandardCharsets.UTF_8));
		byte[] result = md5.digest();
		for (int i = 0, length = result.length; i < length; i++) {
			if (Integer.toHexString(0xFF & result[i]).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & result[i]));
			} else
				md5StrBuff.append(Integer.toHexString(0xFF & result[i]));
		}
		return md5StrBuff.toString();
	}

    /**
     * 输入字符串加盐序列化
     * @param inputString
     * @param saltLength
     * @return
     */
    public static String toEncrypt(String inputString, int saltLength) {
        byte[] salt = getSaltOfASCII(saltLength);
        try {
            return URLEncoder.encode(toEncrypt(inputString, salt), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证加盐字符串是否合法
     *
     * @param inputString
     *            输入的字符串
     * @param encryptString
     *            已存储的字符串
     * @return true:通过检查,false:未通过
     */
    public static boolean checkEncrypt(String inputString, String encryptString, int saltLength) {
        boolean ok;
        try {
            encryptString = URLDecoder.decode(encryptString, StandardCharsets.UTF_8.toString());
            byte[] saltBys = encryptString.substring(0, saltLength).getBytes(StandardCharsets.UTF_8);
            ok = toEncrypt(inputString, saltBys).equals(encryptString);
        } catch (Exception ex) {
            ex.printStackTrace();
            ok = false;
        }
        return ok;
    }

    /**
     * 将客户输入的密码加密
     *
     * @param inputString
     *            客户输入的密码
     * @param salt
     *            盐
     * @return 加密后的字符串
     */
    private static String toEncrypt(String inputString, byte[] salt) {
        StringBuilder encryptString = new StringBuilder();
        md5.reset();
        md5.update(salt);
        md5.update(inputString.getBytes(StandardCharsets.UTF_8));
        byte[] bys = md5.digest();
        for (int i=0,length=salt.length; i<length; i++) {
            encryptString.append((char) salt[i]);
        }
        encryptString.append(Base64.encodeBase64String(bys));
        return encryptString.toString();
    }

    /**
     * 返回指定长度的盐(ASCII码)
     *
     * @param len
     *            长度
     * @return
     */
    private static byte[] getSaltOfASCII(int len) {
        byte[] salt = new byte[len];
        for (int i = 0; i < len; i++) {
            salt[i] = (byte) ((SPECIAL_CHARS_ARRAY[random.nextInt(SPECIAL_CHARS_ARRAY.length)] + '!') & 0x007f);
        }
        return salt;
    }
}