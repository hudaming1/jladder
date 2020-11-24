package org.hum.jladder.adapter.protocol;

import org.hum.jladder.common.util.AESCoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class JladderEncryptCodecHandler extends ChannelDuplexHandler {
	
	private static final short TRANSFER_TYPE = 0;
	private static final long MAGIC_NUMBER = 0x90ABCDEF;
	
	public JladderEncryptCodecHandler() {
	}

	// read -> decrypt
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	if (msg instanceof ByteBuf) {
    		ByteBuf byteBuf = (ByteBuf) msg;
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
    		
    		ctx.fireChannelRead(JladderMessage.buildNormalMessage(id, new String(hostBytes), port, body));
    	} else {
    		ctx.fireChannelRead(msg);
    	}
    }
	
	// write -> encrypt
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof JladderMessage) {
    		JladderMessage jladderMessage = (JladderMessage) msg;

    		byte[] hostBytes4Encrypt = aesEncrypt(jladderMessage.getHost().getBytes());
    		ByteBuf body = jladderMessage.getBody();
    		byte[] bodyArr = new byte[body.readableBytes()];
    		body.writeBytes(bodyArr);
    		body.release();
    		byte[] bodyBytes4Encrypt = aesEncrypt(bodyArr);
    		
    		ByteBuf buf = Unpooled.buffer();
    		buf.writeLong(MAGIC_NUMBER);
    		buf.writeShort(TRANSFER_TYPE);
    		buf.writeLong(jladderMessage.getId());
    		buf.writeInt(hostBytes4Encrypt.length);
    		buf.writeBytes(hostBytes4Encrypt);
    		buf.writeInt(jladderMessage.getPort());
    		buf.writeInt(bodyBytes4Encrypt.length);
    		buf.writeBytes(bodyBytes4Encrypt);
    		
    		ctx.writeAndFlush(buf);
    	} else {
    		ctx.writeAndFlush(msg);
    	}
    }

	private byte[] aesEncrypt(byte[] bytes) {
		return AESCoder.encrypt(bytes);
	}
	
	private byte[] aesDecrypt(byte[] bytes) {
		return AESCoder.decrypt(bytes);
	}
}
