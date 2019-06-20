package org.hum.nettyproxy.common.enumtype;

/**
 * 定义了拦截请求后，对后续相应如何处理的枚举值
 * 
 * @author huming
 */
public enum InterceptorResponseEnum {

	Redirect (1, "重定向"),
	Mock(2, "mock数据"),
	Capture(3, "抓包"),
	;
	
	private int code;
	private String desc;

	InterceptorResponseEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static InterceptorResponseEnum getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (InterceptorResponseEnum respEnum : values()) {
			if (respEnum.code == code) {
				return respEnum;
			}
		}
		return null;
	}
}
