package org.hum.nettyproxy.compoment.interceptor.enumtype;

public enum InterceptorFieldEnum {
	
	Line	("Line", 		"请求行"),
	Header	("Header", 		"请求头"),
	Body	("Body", 		"请求体"),
	;

	private String code;
	private String desc;
	
	InterceptorFieldEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public static InterceptorFieldEnum getEnum(String code) {
		if (code == null || code.trim().isEmpty()) {
			return null;
		}
		for (InterceptorFieldEnum option : values()) {
			if (option.code.equalsIgnoreCase(code)) {
				return option;
			}
		}
		return null;
	}
}
