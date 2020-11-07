package org.hum.jladder.common.util;

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

	private static byte[] _encrypt(byte[] data) throws Exception {
		return _encrypt(data, DefaultKey, DEFAULT_CIPHER_ALGORITHM);
	}

	private static byte[] _encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		return _encrypt(data, k, cipherAlgorithm);
	}

	private static byte[] _encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
		// 实例化
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		// 使用密钥初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, key);
		// 执行操作
		return cipher.doFinal(data);
	}

	private static byte[] _decrypt(byte[] data) throws Exception {
		return _decrypt(data, DefaultKey, DEFAULT_CIPHER_ALGORITHM);
	}

	private static byte[] _decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
		// 还原密钥
		Key k = toKey(key);
		return _decrypt(data, k, cipherAlgorithm);
	}

	private static byte[] _decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
		// 实例化
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		// 使用密钥初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, key);
		// 执行操作
		return cipher.doFinal(data);
	}

	public static byte[] encrypt(byte[] _bytes) {
		try {
			return _encrypt(_bytes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("decrypt error, byte=" + Arrays.toString(_bytes));
		}
	}

	public static byte[] decrypt(byte[] _bytes) {
		try {
			return _decrypt(_bytes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("decrypt error, byte=" + Arrays.toString(_bytes));
		}
	}
}