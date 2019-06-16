package org.hum.nettyproxy.common.enumtype;

import lombok.Getter;

/**
 * netty-proxy-server 转发模式枚举
 * @author hudaming
 */
@Getter
public enum RunModeEnum {

	HttpSimpleProxy(1, "http-simple-prosy", "单节点Http协议转发"),
	HttpInsideServer(11, "http-inside-server", "墙内Http协议转发"), // 需要配合OutsideServer完成转发
	SocksInsideServer(12, "socks-inside-server", "墙内Socks5协议转发"),  // 需要配合OutsideServer完成转发
	OutsideServer(100, "outside-server", "墙外转发服务器"),
	;

	private int code;
	private String desc;
	private String name;
	
	RunModeEnum(int code, String name, String desc) {
		this.code = code;
		this.name = name;
		this.desc = desc;
	}

	public static RunModeEnum getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (RunModeEnum mode : values()) {
			if (mode.code == code) {
				return mode;
			}
		}
		return null;
	}
}
