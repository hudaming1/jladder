package org.hum.nettyproxy.adapter.http.handler;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;
import org.hum.nettyproxy.common.Config;
import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.codec.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.handler.ForwardHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class HttpProxyEncryptHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	private static final ByteBuf CONNECT_PROXY_LINE = Unpooled.wrappedBuffer(ConnectedLine.getBytes());
	
	@Override
	protected void channelRead0(ChannelHandlerContext localCtx, HttpRequest req) throws Exception {

		if (req.getHost() == null || req.getHost().isEmpty()) {
			return;
		}

		// 建立远端转发连接（远端收到响应后，一律转发给本地）
		Bootstrap bootStrap = new Bootstrap();
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.group(localCtx.channel().eventLoop());
		bootStrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new ShakeHanlder(localCtx.channel(), req));
			}
		});
		
		bootStrap.connect(Config.PROXY_HOST, Config.PROXY_PORT).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture remoteFuture) throws Exception {
				// forward request
				byte[] hostBytes = req.getHost().getBytes();
				ByteBuf byteBuf = remoteFuture.channel().alloc().directBuffer();
				byteBuf.writeInt(Constant.MAGIC_NUMBER);
				byteBuf.writeInt(hostBytes.length);
				byteBuf.writeBytes(hostBytes);
				byteBuf.writeShort(req.getPort());
				remoteFuture.channel().writeAndFlush(byteBuf);
			}
		});
	}
	
	private static class ShakeHanlder extends ChannelInboundHandlerAdapter {
		
		private Channel channel;
		private HttpRequest req;
		
		public ShakeHanlder(Channel channel, HttpRequest req) {
			this.channel = channel;
			this.req = req;
		}
		
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	        ByteBuf byteBuf = (ByteBuf) msg; // msg-value -> NettyProxyBuildSuccessMessage
	        if (byteBuf.readInt() != Constant.MAGIC_NUMBER) {
	        	System.out.println("error"); // TODO 告知断开连接
	        	return ;
	        } else if (byteBuf.readInt() != NettyProxyBuildSuccessMessage.SUCCESS) {
	        	System.out.println("error"); // TODO 告知断开连接
	        	return ;
	        }
	        /** 正常情况 **/
	        // 脱壳
	        ctx.pipeline().remove(this);
	        // proxy.response -> browser (仅开启单项转发就够了，因为HTTP是请求/应答协议)
	        ctx.pipeline().addLast(new ForwardHandler(channel)); 
	        // 转发给Proxy
	        ctx.channel().writeAndFlush(req.getByteBuf());
	    }
	}
}
