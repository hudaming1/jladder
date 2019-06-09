package org.hum.nettyproxy.common.codec;

import org.hum.nettyproxy.common.model.NettyProxyConnectMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyProxyConnectMessageCodec {

	public static class Decoder extends ChannelInboundHandlerAdapter {
	
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	ByteBuf byteBuf = (ByteBuf) msg;
	    	NettyProxyConnectMessage message = new NettyProxyConnectMessage();
	    	message.setMagicNum(byteBuf.readInt());
	    	message.setHostLen(byteBuf.readInt());
	    	byte[] bytes = new byte[message.getHostLen()]; // TODO 待优化
	    	byteBuf.readBytes(bytes);
	    	message.setHost(new String(bytes));
	    	message.setPort(byteBuf.readShort());
	        ctx.fireChannelRead(msg);
	    }
	}
}