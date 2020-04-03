package org.hum.nettyproxy.compoment.interceptor.enumtype;

import io.netty.util.internal.StringUtil;

public enum ActionTypeEnum {

	Update ("update"),
	Replace ("replace"),
	Delete ("delete"),
	Add ("add"),
	Print ("print"),
	;
	
	private String name;
	
	ActionTypeEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static ActionTypeEnum getEnum(String actionType) {
		if (StringUtil.isNullOrEmpty(actionType)) {
			return null;
		}
		for (ActionTypeEnum actionTypeEnum : values()) {
			if (actionTypeEnum.name.equalsIgnoreCase(actionType.trim())) {
				return actionTypeEnum;
			}
		}
		return null;
	}
}
