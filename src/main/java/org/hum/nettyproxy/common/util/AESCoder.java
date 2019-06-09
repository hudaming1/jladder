package org.hum.nettyproxy.common.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCoder {

	private static final String KEY_ALGORITHM = "AES";
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";// 默认的加密算法
	private static final byte[] DefaultKey = "F8d2L@@jd.3Ufh1S".getBytes();

	public static byte[] initSecretKey() {

		// 返回生成指定算法密钥生成器的 KeyGenerator 对象
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(DefaultKey);
			// 初始化此密钥生成器，使其具有确定的密钥大小
			// AES 要求密钥长度为 128
			kg.init(128, random);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}
		// 生成一个密钥
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}

	private static Key toKey(byte[] key) {
		// 生成密钥
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}

	public static byte[] encrypt(byte[] data) throws Exception {
		return encrypt(data, DefaultKey, DEFAULT_CIPHER_ALGORITHM);
	}

	public static byte[] encrypt(byte[] data, Key key) throws Exception {
		return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
	}

	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
	}

	public static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		return encrypt(data, k, cipherAlgorithm);
	}

	public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
		// 实例化
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		// 使用密钥初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, key);
		// 执行操作
		return cipher.doFinal(data);
	}

	public static byte[] decrypt(byte[] data) throws Exception {
		return decrypt(data, DefaultKey, DEFAULT_CIPHER_ALGORITHM);
	}

	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
	}

	public static byte[] decrypt(byte[] data, Key key) throws Exception {
		return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
	}

	public static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		return decrypt(data, k, cipherAlgorithm);
	}

	public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
		// 实例化
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		// 使用密钥初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, key);
		// 执行操作
		return cipher.doFinal(data);
	}

	public static void main(String[] args) throws Exception {
		// byte[] b = new byte[] { 22, 3, 1, 0, -67, 1, 0, 0, -71, 3, 3, 54, 62, 10, 96, -64, 21, -93, 21, -36, 85, -23, -86, 10, -10, 26, 23, -83, -34, -32, 23, 16, -48, 84, 119, -30, -74, -105, -56, 29, -106, 43, 103, 0, 0, 30, -64, 43, -64, 47, -52, -87, -52, -88, -64, 44, -64, 48, -64, 10, -64, 9, -64, 19, -64, 20, 0, 51, 0, 57, 0, 47, 0, 53, 0, 10, 1, 0, 0, 114, 0, 0, 0, 18, 0, 16, 0, 0, 13, 119, 119, 119, 46, 98, 97, 105, 100, 117, 46, 99, 111, 109, 0, 23, 0, 0, -1, 1, 0, 1, 0, 0, 10, 0, 10, 0, 8, 0, 29, 0, 23, 0, 24, 0, 25, 0, 11, 0, 2, 1, 0, 0, 35, 0, 0, 0, 16, 0, 14, 0, 12, 2, 104, 50, 8, 104, 116, 116, 112, 47, 49, 46, 49, 0, 5, 0, 5, 1, 0, 0, 0, 0, -1, 3, 0, 0, 0, 13, 0, 24, 0, 22, 4, 3, 5, 3, 6, 3, 8, 4, 8, 5, 8, 6, 4, 1, 5, 1, 6, 1, 2, 3, 2, 1 };
		byte[] b = new byte[] { 53, -7, -16, -19, -112, -22, -87, 108, 121, -20, 75, 18, 71, 75, -71, -11, 1, -69, 27, -38, -14, 98, 109, 19, -81, 5, -65, -92, 51, -120, -30, -15, 96, 31, 73, -76, 83, 112, 120, -14, 101, -112, -21, 83, 14, -127, -85, 78, 14, 13, -7, -47, 101, 63, 55, -2, -106, 24, -116, 104, -86, 106, 108, 33, 115, -93, -68, 35, -61, -128, 5, 126, -115, 25, 93, -9, 85, -80, 109, 105, 103, -72, 117, -93, 68, 25, 89, 86, -98, 8, 45, -124, -33, -68, 56, 44, -78, -92, 109, 106, 86, -12, -57, 95, -34, 76, -128, 127, 48, -37, 74, 68, -59, 30, -118, 33, 0, 6, -127, 107, 39, 11, -71, -42, -16, 48, 103, 75, 57, -120, 106, 111, -84, 120, 47, -122, 26, 47, 20, 103, 3, 109, -15, 121, 34, 99, 91, -1, 118, -43, 18, 76, -114, 0, -113, 84, -124, -48, 15, -114, 45, -110, -65, -10, 109, 41, -70, 91, -102, 120, -24, 104, 113, -2, 21, -59, 120, 114, 19, -53, 77, 87, 106, 102, -56, 103, -68, -119, -97, -125, 2, 53, 60, -111, -43, 104, 114, 93, 95, -96, -61, 43, -21, 19, 109, -114, -108, -40, -35, -18, -52, -128, 91, -6, -117, 18, -57, -47, -117, 58, 34, 73, 73, 17, 93, -77, 13, -44, 30, -110, -92, -64, 105, 38, -118, 44, -42, -54, -72, 2, -51, 18, -11, 45, -44, 81, -92, -3, -120, 28, -36, -80, -8, 127, 103, -111, -32, -104, -101, -71, -9, -100, -48, 39, -123, 30, -64, 101, 41, 91, 64, -80, -42, -58, -78, 102, 35, 127, 70, 81, 8, 126, 23, 34, -8, 102, -17, 5, -88, -125, 39, 92, 41, -15, -97, -115, 69, 5, 9, -29, -70, -88, -67, 118, -2, 55, 0 };
		System.out.println(b.length);
		byte[] bb = encrypt(b);
		System.out.println(bb.length);
		System.out.println("source:" + Arrays.toString(b));
		System.out.println("encrypt:" + Arrays.toString(bb));
		System.out.println("decripy:" + Arrays.toString(decrypt(bb)));
		System.out.println(new String(new byte[] { 61, -67, -65, -101, -96, 90, 89, -33, -124, 46, 47, -97, 12, 53, 74, 8 }));
	}
}