package org.hum.nettyproxy.common.enumtype;

public enum HttpMethodEnum {
	POST("post".getBytes(), "POST".getBytes()), 
	GET("get".getBytes(), "GET".getBytes()), 
	PUT("put".getBytes(), "PUT".getBytes()), 
	PATCH("patch".getBytes(), "PATCH".getBytes()), 
	DELETE("delete".getBytes(), "DELETE".getBytes()), 
	HEAD("head".getBytes(), "HEAD".getBytes()), 
	OPTIONS("options".getBytes(), "OPTIONS".getBytes()), 
	;
	
	private byte[] upper;
	private byte[] lower;
	
	HttpMethodEnum(byte[] upper, byte[] lower) {
		this.upper = upper;
		this.lower = lower;
	}
	
	private static byte[][] byteArr;
	static {
		byteArr = new byte[14][];
		int i = 0;
		for(HttpMethodEnum methodEnum : values()) {
			byteArr[i] = methodEnum.lower;
			byteArr[i + 1] = methodEnum.upper;
			i += 2;
		}
	}
	
	public static byte[][] getByteArray() {
		return byteArr;
	}
}
