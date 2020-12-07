package org.jladder.adapter.protocol.message;

public class JladderMessage {

	private String clientIden;
	private String host;
	private int port;

	JladderMessage(String clientIden, String host, int port) {
		this.clientIden = clientIden;
		this.host = host;
		this.port = port;
	}
	
	public String getClientIden() {
		return clientIden;
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
