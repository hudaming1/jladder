package org.jladder.adapter.protocol.message;

import org.jladder.adapter.protocol.crypto.CryptoFactory;
import org.jladder.adapter.protocol.enumtype.JladderMessageTypeEnum;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class JladderDataMessage extends JladderMessage {

	private String clientIden;
	private boolean bodyNeedEncrypt;
	private String host;
	private int port;
	private ByteBuf body;

	JladderDataMessage(String clientIden, boolean bodyNeedEncrypt, String host, int port, ByteBuf body) {
		super(JladderMessageTypeEnum.Data.getCode(), clientIden, host, port);
		this.body = body;
		this.bodyNeedEncrypt = bodyNeedEncrypt;
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

	public ByteBuf getBody() {
		return body;
	}
	
	public boolean isBodyNeedEncrypt() {
		return bodyNeedEncrypt;
	}

	@Override
	public ByteBuf toByteBuf() {

		boolean bodyNeedEncrypt = this.bodyNeedEncrypt;
		String host = this.host;
		int port = this.port;
		String clientIden = this.clientIden;
		
		byte[] hostBytes4Encrypt = CryptoFactory.get().encrypt(host.getBytes());
		ByteBuf body = this.body.retain();
		
		byte[] bodyArr = new byte[body.readableBytes()];
		body.readBytes(bodyArr);
		body.release();
		// TODO 如果不需要加密，则直接用CompositeByteBuf组合即可
		byte[] bodyBytes4Encrypt = bodyNeedEncrypt ? CryptoFactory.get().encrypt(bodyArr) : bodyArr;
		
		ByteBuf buf = Unpooled.buffer();
		buf.writeLong(MAGIC_NUMBER);
		buf.writeShort(TRANSFER_TYPE);
		buf.writeInt(clientIden.length());
		buf.writeBytes(clientIden.getBytes());
		buf.writeInt(hostBytes4Encrypt.length);
		buf.writeBytes(hostBytes4Encrypt);
		buf.writeInt(port);
		buf.writeBoolean(bodyNeedEncrypt);
		buf.writeInt(bodyBytes4Encrypt.length);
		buf.writeBytes(bodyBytes4Encrypt);
		return null;
	}
	
}
