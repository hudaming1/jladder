package org.jladder.core.enumtype;

public enum JladderForwardWorkerStatusEnum {
	
	Running(1, "运行中"),
	Terminated(-1, "停止"),
	;

	private int status;
	private String desc;
	
	JladderForwardWorkerStatusEnum(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public int getStatus() {
		return status;
	}

	public String getDesc() {
		return desc;
	}
	
	public static JladderForwardWorkerStatusEnum getEnum(Integer status) {
		if (status == null) {
			return null;
		}
		for (JladderForwardWorkerStatusEnum statusEnum : values()) {
			if (statusEnum.status == status) {
				return statusEnum;
			}
		}
		return null;
	}
}
