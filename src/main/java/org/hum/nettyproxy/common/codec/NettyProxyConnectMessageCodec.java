package org.hum.nettyproxy.common.codec;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;

public class NettyProxyConnectMessageCodec {

	@Data
	public static class NettyProxyConnectMessage implements Serializable {
	
		private static final long serialVersionUID = 1L;
		private int magicNum;
		private int hostLen;
		private String host;
		private short port;
		
		public NettyProxyConnectMessage() { }
		
		public NettyProxyConnectMessage(int magicNum, int hostLen, String host, short port) {
			super();
			this.magicNum = magicNum;
			this.hostLen = hostLen;
			this.host = host;
			this.port = port;
		}
	}

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
	        ctx.fireChannelRead(message);
	    }
	}
}