package org.hum.nettyproxy.test.https_client.tls.enumtype;

public enum TLS_ContentTypeEnum {
	
	// 改变密码格式协议(change_cipher_spec)
	CHANGE_CIPHER_SPEC(20),
	// 警告协议(alert)
	ALERT(21),
	// 握手协议(handshake): 22
	HANDSHAKE(22),
	// 应用数据协议(application_data)
	APPLICATION_DATA(23),
	;
	
	TLS_ContentTypeEnum(int code) {
		this.code = (byte) code;
	}
	
	private byte code;

	public byte getCode() {
		return code;
	}
	
	public static TLS_ContentTypeEnum getEnum(Integer code) {
		if (code == null) {
			throw new IllegalArgumentException("unknown code[" + code + "]");
		}
		for (TLS_ContentTypeEnum contentType : values()) {
			if (contentType.code == code) {
				return contentType;
			}
		}
		return null;
	}
}
