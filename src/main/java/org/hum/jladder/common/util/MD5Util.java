package org.hum.jladder.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	static final char hexDigitsLower[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 对字符串 MD5 无盐值加密
	 * 
	 * @param plainText 传入要加密的字符串
	 * @return MD5加密后生成32位(小写字母+数字)字符串
	 */
	public static String MD5Lower(String plainText) {
		try {
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest md = MessageDigest.getInstance("MD5");

			// 使用指定的字节更新摘要
			md.update(plainText.getBytes());

			return new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 对字符串 MD5 加密
	 * 
	 * @param plainText 传入要加密的字符串
	 * @return MD5加密后生成32位(大写字母+数字)字符串
	 */
	public static String MD5Upper(String plainText) {
		try {
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest md = MessageDigest.getInstance("MD5");

			// 使用指定的字节更新摘要
			md.update(plainText.getBytes());

			// 获得密文
			byte[] mdResult = md.digest();
			// 把密文转换成十六进制的字符串形式
			int j = mdResult.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = mdResult[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 对字符串 MD5 加盐值加密
	 * 
	 * @param plainText 传入要加密的字符串
	 * @param saltValue 传入要加的盐值
	 * @return MD5加密后生成32位(小写字母+数字)字符串
	 */
	public static String MD5Lower(String plainText, String saltValue) {
		try {
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest md = MessageDigest.getInstance("MD5");

			// 使用指定的字节更新摘要
			md.update(plainText.getBytes());
			md.update(saltValue.getBytes());

			// digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值。1 固定值
			return new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 对字符串 MD5 加盐值加密
	 * 
	 * @param plainText 传入要加密的字符串
	 * @param saltValue 传入要加的盐值
	 * @return MD5加密后生成32位(大写字母+数字)字符串
	 */
	public static String MD5Upper(String plainText, String saltValue) {
		try {
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest md = MessageDigest.getInstance("MD5");

			// 使用指定的字节更新摘要
			md.update(plainText.getBytes());
			md.update(saltValue.getBytes());

			// 获得密文
			byte[] mdResult = md.digest();
			// 把密文转换成十六进制的字符串形式
			int j = mdResult.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = mdResult[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * MD5加密后生成32位(小写字母+数字)字符串 同 MD5Lower() 一样
	 */
	public final static String MD5(String plainText) {
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");

			mdTemp.update(plainText.getBytes("UTF-8"));

			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigitsLower[byte0 >>> 4 & 0xf];
				str[k++] = hexDigitsLower[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 校验MD5码
	 * 
	 * @param text 要校验的字符串
	 * @param md5  md5值
	 * @return 校验结果
	 */
	public static boolean valid(String text, String md5) {
		return md5.equals(MD5(text)) || md5.equals(MD5(text).toUpperCase());
	}
}
