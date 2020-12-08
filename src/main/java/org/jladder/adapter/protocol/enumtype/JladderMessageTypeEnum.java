package org.jladder.adapter.protocol.enumtype;

public enum JladderMessageTypeEnum {
	
	Data(1, "传输数据"),
	Disconnect(2, "断开连接"),
	;

	private int code;
	private String desc;

	JladderMessageTypeEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static JladderMessageTypeEnum getEnum(Short code) {
		if (code == null) {
			return null;
		}
		return getEnum(code.intValue());
	}
	
	public static JladderMessageTypeEnum getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (JladderMessageTypeEnum typeEnum : values()) {
			if (code == typeEnum.code) {
				return typeEnum;
			}
		}
		return null;
	}
}
