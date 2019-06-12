package org.hum.nettyproxy.common.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtil {

	private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);
	
	public static boolean isReachable(String host, int port, int connectTimeout) {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), connectTimeout);
			return true;
		} catch (IOException ce) {
			return false;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					logger.error("close socket occured exception", e);
				}
			}
		}
	}
}
