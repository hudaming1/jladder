package org.jladder.core.executor;

import java.util.Arrays;

import org.jladder.core.message.JladderMessage;
import org.jladder.core.serial.JladderSerialization;
import org.jladder.core.serial.SimpleJladderSerialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@Slf4j
@Sharable
public class JladderCryptoOutHandler extends ChannelOutboundHandlerAdapter {
	
	private final JladderSerialization serialization = new SimpleJladderSerialization();
	
	// write -> encrypt
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof JladderMessage) {
    		JladderMessage jladderMsg = (JladderMessage) msg;
    		ByteBuf byteBuf = serialization.serial(jladderMsg);
    		// ④ 将jladder消息序列化成ByteBuf，然后由inside将JladderMessage发送给outside 
    		// ⑩ 将jladder消息序列化成ByteBuf，然后由outside消息发送给inside
    		// 3. 将jladder消息序列化成ByteBuf，然后由inside将JladderMessage发送给outside
    		// 9. 将jladder消息序列化成ByteBuf，然后由outside消息发送给inside
    		
    		// -----debug
    		byteBuf.markReaderIndex();
    		byte[] bytes = new byte[byteBuf.readableBytes()];
    		byteBuf.readBytes(bytes);
    		byteBuf.resetReaderIndex();
    		// ---debug
    		// TODO 关注一下④和⑤的发送长度和收到长度不一致的问题
    		log.debug("④/⑩/3/9[" + jladderMsg.getClientIden() + "]将封装后消息输出给对端，消息总长度=" + byteBuf.readableBytes() + ", refCnt=" + byteBuf.refCnt() + "，消息内容=" + Arrays.toString(bytes)); 
    		ctx.writeAndFlush(byteBuf);
    		log.debug("========> refCnt=" + byteBuf.refCnt());
    	} else {
    		ctx.writeAndFlush(msg);
    	}
		// ReferenceCountUtil.release(msg);
    }

}
