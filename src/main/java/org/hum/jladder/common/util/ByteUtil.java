package org.hum.jladder.common.util;

public class ByteUtil {

	
	public static boolean isEquals(byte[] arr1, byte[] arr2) {
		int cursor = 0;
		while (cursor < arr2.length) {
			if (arr1[cursor] != arr2[cursor]) {
				return false;
			}
			cursor ++ ;
		}
		return true;
	}
}
