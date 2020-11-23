package org.hum.jladder.adapter.protocol.encrypt;

import org.hum.jladder.adapter.protocol.JladderMessage;
import org.hum.jladder.common.util.AESCoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DefaultEncStrategy implements EncStrategy {

	private static final short TRANSFER_TYPE = 0;
	private static final long MAGIC_NUMBER = 0x90ABCDEF;
	
	@Override
	public ByteBuf encrypt(JladderMessage message) {
		// TODO 分配直接内存 ctx.channel().alloc().directBuf();
		
		byte[] hostBytes4Encrypt = aesEncrypt(message.getHost().getBytes());
		ByteBuf body = message.getBody();
		byte[] bodyArr = new byte[body.readableBytes()];
		body.writeBytes(bodyArr);
		body.release();
		byte[] bodyBytes4Encrypt = aesEncrypt(bodyArr);
		
		ByteBuf buf = Unpooled.buffer();
		buf.writeLong(MAGIC_NUMBER);
		buf.writeShort(TRANSFER_TYPE);
		buf.writeLong(message.getId());
		buf.writeInt(hostBytes4Encrypt.length);
		buf.writeBytes(hostBytes4Encrypt);
		buf.writeInt(message.getPort());
		buf.writeInt(bodyBytes4Encrypt.length);
		buf.writeBytes(bodyBytes4Encrypt);
		return buf;
	}
	
	private byte[] aesEncrypt(byte[] bytes) {
		return AESCoder.encrypt(bytes);
	}
	
	private byte[] aesDecrypt(byte[] bytes) {
		return AESCoder.decrypt(bytes);
	}

	@Override
	public JladderMessage decrypt(ByteBuf byteBuf) {
		byteBuf.skipBytes(8); // skip magic_number
		byteBuf.skipBytes(2); // skip type
		long id = byteBuf.readLong();
		// read host
		int hostLen = byteBuf.readInt();
		byte[] hostBytes4Encrypt = new byte[hostLen];
		byteBuf.readBytes(hostBytes4Encrypt);
		byte[] hostBytes = aesDecrypt(hostBytes4Encrypt);
		// read port
		int port = byteBuf.readInt();
		// read
		int bodyLen = byteBuf.readInt();
		byte[] bodyBytes4Encrypt = new byte[bodyLen];
		byteBuf.readBytes(bodyBytes4Encrypt);
		byte[] bodyBytes = aesDecrypt(bodyBytes4Encrypt);
		ByteBuf body = Unpooled.buffer(bodyLen);
		body.readBytes(bodyBytes);
		
		return new JladderMessage(id, new String(hostBytes), port, body);
	}
}
