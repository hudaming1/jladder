package org.hum.nettyproxy.common.exception;

public class NettyProxyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NettyProxyException(String msg) {
		super(msg);
	}

	public NettyProxyException(String msg, Throwable error) {
		super(msg, error);
	}
}
