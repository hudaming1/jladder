package org.hum.nettyproxy.common.codec;

import org.hum.nettyproxy.common.Constant;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

public class IODecoder extends DelimiterBasedFrameDecoder {

	// 防止沾包分隔符
	private static ByteBuf delimiter = Unpooled.copiedBuffer(Constant.FIXED_DERTIMED.getBytes()); // 沾包 分割符 \n
	private static int maxFrameLength = 65552;// 数据大小

	public IODecoder() {
		super(maxFrameLength, delimiter);
	}

	/**
	 * 重新 自定义解码
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
		System.out.println("IODecoder.decode");
		// 对数据 buffer 解码
		return super.decode(ctx, buffer);
	}
}
