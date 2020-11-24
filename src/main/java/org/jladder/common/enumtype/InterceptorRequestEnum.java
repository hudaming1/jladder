package org.jladder.common.enumtype;

/**
 * 拦截规则类型：定义了HttpProxy可以按照哪几个维度来进行拦截请求
 * @author huming
 */
public enum InterceptorRequestEnum {
	
	Host(1, 0, "根据Host拦截"), // 仅比对header中的Host字段
	URL(2, 10, "根据完整URL拦截"), // 比对header中的Host字段和请求行中的URI
	URLParams(3, 20, "通过URL及请求参数") // 如果get方法，则比对Host和URI；反之比对Host、URI和RequestBody
	// TODO 这里希望可以支持自定义扩展，需要考虑如何设计了......
	;

	private int code;
	/**
	 * 匹配优先级：数字越大代表优先级越高
	 * <pre>
	 *    1.考虑在匹配是，一个Request可能匹配到多个规则，则仅命中priority最高的规则。
	 * 	  2.在匹配时，根据优先级从高到底匹配，一旦命中，则不会匹配后续规则。
	 * </pre>
	 */
	private int priority;
	private String desc;
	
	InterceptorRequestEnum(int code, int priority, String desc) {
		this.code = code;
		this.priority = priority;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public int getPriority() {
		return priority;
	}

	public String getDesc() {
		return desc;
	}
	
	public static InterceptorRequestEnum getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (InterceptorRequestEnum type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return null;
	}
}
