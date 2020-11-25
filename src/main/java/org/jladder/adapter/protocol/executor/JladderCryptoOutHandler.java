package org.jladder.adapter.protocol.executor;

import org.jladder.adapter.protocol.JladderMessage;
import org.jladder.common.util.AESCoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@Sharable
public class JladderCryptoOutHandler extends ChannelOutboundHandlerAdapter {
	
	private static final short TRANSFER_TYPE = 0;
	private static final long MAGIC_NUMBER = 0x90ABCDEF;
	
	// write -> encrypt
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof JladderMessage) {
    		JladderMessage jladderMessage = (JladderMessage) msg;

    		byte[] hostBytes4Encrypt = aesEncrypt(jladderMessage.getHost().getBytes());
    		ByteBuf body = jladderMessage.getBody().retain();
    		
    		byte[] bodyArr = new byte[body.readableBytes()];
    		body.readBytes(bodyArr);
    		body.release();
    		// TODO 如果不需要加密，则直接用CompositeByteBuf组合即可
    		byte[] bodyBytes4Encrypt = jladderMessage.isBodyNeedEncrypt() ? aesEncrypt(bodyArr) : bodyArr;
    		
    		ByteBuf buf = Unpooled.buffer();
    		buf.writeLong(MAGIC_NUMBER);
    		buf.writeShort(TRANSFER_TYPE);
    		buf.writeLong(jladderMessage.getId());
    		buf.writeInt(hostBytes4Encrypt.length);
    		buf.writeBytes(hostBytes4Encrypt);
    		buf.writeInt(jladderMessage.getPort());
    		buf.writeBoolean(jladderMessage.isBodyNeedEncrypt());
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
}
