package org.jladder.common.enumtype;

public enum JladderEventEnum {
	
	InsideActive(0, ""),
	
	InsideHttpParsed(100, ""),
	InsideHttspParsed(200, ""),
	InsideSocksParsed(300, ""),
	;
	
	private int code;
	private String desc;

	JladderEventEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public static JladderEventEnum getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (JladderEventEnum eventType : values()) {
			if (eventType.code == code) {
				return eventType;
			}
		}
		return null;
	}
}
