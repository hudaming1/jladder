package org.hum.nettyproxy.common.codec;

import java.io.Serializable;

import org.hum.nettyproxy.common.Constant;

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
		
		public boolean isHttps() {
			// FIXME 判断https仅从443端口不严谨，应该使用单独一个字段标识，根据前端传入的method判断更加严谨
			return Constant.DEFAULT_HTTPS_PORT == port;
		}
		
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
	
	public static class EncoderUtil {
		public static ByteBuf encode(ByteBuf byteBuf, byte[] host, short port) {
			byteBuf.writeInt(Constant.MAGIC_NUMBER);
			byteBuf.writeInt(host.length);
			byteBuf.writeBytes(host);
			byteBuf.writeShort(port);
			return byteBuf;
		}
	}
}