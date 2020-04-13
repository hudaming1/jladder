package org.hum.nettyproxy.compoment.interceptor.enumtype;

public enum InterceptorTypeEnum {
	
	Request("request", "请求"),
	Response("response", "请求"),
	;

	private String code;
	private String desc;
	
	InterceptorTypeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public static InterceptorTypeEnum getType(String code) {
		if (code == null || code.trim().isEmpty()) {
			return null;
		}
		for (InterceptorTypeEnum option : values()) {
			if (option.code.equalsIgnoreCase(code)) {
				return option;
			}
		}
		return null;
	}
}
