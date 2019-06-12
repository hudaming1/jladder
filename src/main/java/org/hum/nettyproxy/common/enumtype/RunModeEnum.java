package org.hum.nettyproxy.common.enumtype;

import org.hum.nettyproxy.ServerRun.Starter;
import org.hum.nettyproxy.adapter.http.NettyHttpProxyStarter;

/**
 * netty-proxy-server 转发模式枚举
 * @author hudaming
 */
public enum RunModeEnum {

	HttpSimpleProxy(1, "单节点Http协议转发", new NettyHttpProxyStarter()),
	HttpInsideServer(11, "墙内Http协议转发", null), // 需要配合OutsideServer完成转发
	SocksInsideServer(12, "墙内Socks5协议转发", null),  // 需要配合OutsideServer完成转发
	OutsideServer(100, "墙外转发服务器", null),
	;

	private int code;
	private String desc;
	private Starter starter;
	
	RunModeEnum(int code, String desc, Starter starter) {
		this.code = code;
		this.desc = desc;
		this.starter = starter;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public Starter getStarter() {
		return starter;
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
