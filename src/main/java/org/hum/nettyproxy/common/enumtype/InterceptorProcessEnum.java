package org.hum.nettyproxy.common.enumtype;

/**
 * 定义了拦截请求后，对后续相应如何处理的枚举值
 * 
 * @author huming
 */
public enum InterceptorProcessEnum {

	Redirect (1, "重定向"),
	Mock(2, "mock数据"),
	Capture(3, "抓包"),
	ModifyRequest(4, "修改请求"), // 例如修改某一项参数，或增加header之类的
	;
	
	private int code;
	private String desc;

	InterceptorProcessEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static InterceptorProcessEnum getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (InterceptorProcessEnum respEnum : values()) {
			if (respEnum.code == code) {
				return respEnum;
			}
		}
		return null;
	}
}
