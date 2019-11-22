package org.hum.nettyproxy.adapter.console;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

public abstract class NettyHttpUriHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private String uri;
	
	public NettyHttpUriHandler(String uri) {
		this.uri = uri;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		
		// 由于集成了SimpleChannelInboundHandler，因此在方法结束后会多一次release，因此这里手动retain一次，避免产生错误日志
		ReferenceCountUtil.retain(msg, 1);
		
		if (msg.uri().contains(uri)) {
			process(ctx, msg);
			return ;
		}
		
		ctx.fireChannelRead(msg);
	}
	
	public abstract void process(ChannelHandlerContext ctx, FullHttpRequest req);
}
