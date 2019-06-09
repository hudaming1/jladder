package org.hum.nettyproxy.common.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class NettyProxyConnectMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private int magicNum;
	private int hostLen;
	private String host;
	private short port;
	
	public NettyProxyConnectMessage() { }
	
	public NettyProxyConnectMessage(int magicNum, int hostLen, String host, short port) {
		super();
		this.magicNum = magicNum;
		this.hostLen = hostLen;
		this.host = host;
		this.port = port;
	}
}
