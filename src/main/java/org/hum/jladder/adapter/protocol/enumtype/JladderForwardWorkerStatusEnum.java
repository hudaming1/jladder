package org.hum.jladder.adapter.protocol.enumtype;

public enum JladderForwardWorkerStatusEnum {
	
	Running(1, "启动"),
	Starting(2, "启动中"),
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
