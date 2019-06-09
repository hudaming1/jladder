package org.hum.nettyproxy.common.handler;

import java.util.Arrays;

import org.hum.nettyproxy.common.util.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
			System.out.println("current_readable_size=" + bytebuff.readableBytes() + ", byteBuf=" + bytebuff);
			if (bytebuff.readableBytes() >= 4) {
				byte[] arr = new byte[bytebuff.readInt()];
				System.out.println("decode.len=" + arr.length + ", avaiable.len=" + bytebuff.readableBytes());
				if (bytebuff.readableBytes() < arr.length) {
					bytebuff.resetReaderIndex();
					System.out.println("cann't be read!");
					bytebuff.retain(); // TODO 如果不满足读取条件，怎么将bytebuf还回去，而不做抛弃处理呢？
					return;
				}
				System.out.println("can be read!");
				try {
					bytebuff.readBytes(arr);
					System.out.println("decode.arr=" + Arrays.toString(arr));
					byte[] decrypt = Utils.decrypt(arr);
					ByteBuf byteBuf = ctx.alloc().buffer();
					byteBuf.writeBytes(decrypt);
					pipeChannel.writeAndFlush(byteBuf);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
