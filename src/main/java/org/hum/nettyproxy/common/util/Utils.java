package org.hum.nettyproxy.common.util;

import java.util.Arrays;

public class Utils {

	public static byte[] encrypt(byte[] _bytes) {
		try {
			return AESCoder.encrypt(_bytes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("decrypt error, byte=" + Arrays.toString(_bytes));
		}
	}

	public static byte[] decrypt(byte[] _bytes) {
		try {
			return AESCoder.decrypt(_bytes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("decrypt error, byte=" + Arrays.toString(_bytes));
		}
	}
}
