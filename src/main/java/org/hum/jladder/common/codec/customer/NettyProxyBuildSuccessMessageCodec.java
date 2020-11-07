package org.hum.jladder.common.codec.customer;

import java.io.Serializable;

import org.hum.jladder.common.Constant;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;

public class NettyProxyBuildSuccessMessageCodec {

	@Data
	public static class NettyProxyBuildSuccessMessage implements Serializable {
	
		private static final long serialVersionUID = 1L;
		public static final int SUCCESS = 1;
		private static final NettyProxyBuildSuccessMessage SUCCESS_MSG = new NettyProxyBuildSuccessMessage(Constant.MAGIC_NUMBER, SUCCESS);
		private int magicNuml;
		private int code;
		
		public NettyProxyBuildSuccessMessage() { }
	
		public NettyProxyBuildSuccessMessage(int magicNuml, int code) {
			this.magicNuml = magicNuml;
			this.code = code;
		}
		
		public static ByteBuf build(Channel channel) {
			ByteBuf byteBuf = channel.alloc().directBuffer();
			byteBuf.writeInt(SUCCESS_MSG.magicNuml);
			byteBuf.writeInt(SUCCESS_MSG.code);
			return byteBuf;
		}
	}
}
