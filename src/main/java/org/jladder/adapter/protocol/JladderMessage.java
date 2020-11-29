package org.jladder.adapter.protocol;

import io.netty.buffer.ByteBuf;

public class JladderMessage {

	private long id;
	private String clientIden;
	private boolean bodyNeedEncrypt;
	private String host;
	private int port;
	private ByteBuf body;

	private JladderMessage(String clientIden, boolean bodyNeedEncrypt, long id, String host, int port, ByteBuf body) {
		this.clientIden = clientIden;
		this.id = id;
		this.host = host;
		this.port = port;
		this.body = body;
		this.bodyNeedEncrypt = bodyNeedEncrypt;
	}
	
	public String getClientIden() {
		return clientIden;
	}

	public long getId() {
		return id;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public ByteBuf getBody() {
		return body;
	}
	
	public boolean isBodyNeedEncrypt() {
		return bodyNeedEncrypt;
	}
	
	public static JladderMessage buildNeedEncryptMessage(String clientIden, String host, int port, ByteBuf body) {
		return buildNeedEncryptMessage(clientIden, System.nanoTime(), host, port, body);
	}

	public static JladderMessage buildNeedEncryptMessage(String clientIden, long id, String host, int port, ByteBuf body) {
		return new JladderMessage(clientIden, true, id, host, port, body);
	}

	public static JladderMessage buildUnNeedEncryptMessage(String clientIden, String host, int port, ByteBuf body) {
		return new JladderMessage(clientIden, false, System.nanoTime(), host, port, body);
	}

	public static Object buildDisconnectMessage(String clientIden2) {
		// TODO Auto-generated method stub
		return null;
	}
}
