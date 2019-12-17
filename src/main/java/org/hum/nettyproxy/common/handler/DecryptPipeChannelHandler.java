package org.hum.nettyproxy.common.handler;

import java.util.Arrays;

import org.hum.nettyproxy.common.util.AESCoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DecryptPipeChannelHandler extends ChannelInboundHandlerAdapter {

	private Channel pipeChannel;

	public DecryptPipeChannelHandler(Channel channel) {
		this.pipeChannel = channel;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (pipeChannel.isActive()) {
			ByteBuf bytebuff = (ByteBuf) msg;
			if (bytebuff.readableBytes() > 0) {
				byte[] arr = new byte[bytebuff.readableBytes()];
				try {
					bytebuff.readBytes(arr);
					byte[] decrypt = AESCoder.decrypt(arr);
					ByteBuf byteBuf = ctx.alloc().buffer();
					byteBuf.writeBytes(decrypt);
					pipeChannel.writeAndFlush(byteBuf);
				} catch (Exception e) {
					System.out.println(Arrays.toString(arr));
					e.printStackTrace();
				} finally {
					ReferenceCountUtil.release(msg);
				}
			}
		}
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
    }
}
