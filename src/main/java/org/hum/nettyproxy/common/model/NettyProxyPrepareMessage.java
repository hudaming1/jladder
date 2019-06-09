package org.hum.nettyproxy.common.model;

import java.io.Serializable;

import org.hum.nettyproxy.common.Constant;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

@Data
public class NettyProxyPrepareMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int SUCCESS = 1;
	private static final NettyProxyPrepareMessage SUCCESS_MSG = new NettyProxyPrepareMessage(Constant.MAGIC_NUMBER, SUCCESS);
	private int magicNuml;
	private int code;
	
	public NettyProxyPrepareMessage() { }

	public NettyProxyPrepareMessage(int magicNuml, int code) {
		this.magicNuml = magicNuml;
		this.code = code;
	}
	
	public static ByteBuf buildSuccess() {
		// TODO 待优化
		ByteBuf byteBuf = Unpooled.directBuffer();
		byteBuf.writeInt(SUCCESS_MSG.magicNuml);
		byteBuf.writeInt(SUCCESS_MSG.code);
		return byteBuf;
	}
}
