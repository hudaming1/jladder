package org.hum.nettyproxy.adapter.http.simpleproxy;

import java.net.InetSocketAddress;
import java.util.Map;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;
import org.hum.nettyproxy.common.util.ByteBufWebUtil;
import org.hum.nettyproxy.common.util.HttpUtil;
import org.hum.nettyproxy.compoment.auth.AuthManager;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class HttpAuthorityHandler extends SimpleChannelInboundHandler<HttpRequest> {
	
	private final AuthManager AuthManager = new AuthManager();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		
		System.out.println(AuthManager);
		
		InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().localAddress(); 
		// 如果是登录请求，则优先处理
		if (msg.getUri().contains("/submit_login")) {
			Map<String, String> params = HttpUtil.parseBody2FormData(msg.getBody());
			if (!AuthManager.login(socketAddr.getHostString(), params.get("name"), params.get("pass"))) {
				ctx.writeAndFlush(ByteBufWebUtil.readFileFromWebapps(ctx.alloc().directBuffer(), "403.html")).addListener(ChannelFutureListener.CLOSE);
				return ;
			}
			ctx.writeAndFlush(ByteBufWebUtil.readFileFromWebapps(ctx.alloc().directBuffer(), "index.html")).addListener(ChannelFutureListener.CLOSE);
			return ;
		}
		
		// 其他请求，则判断是否已登录
		if (!AuthManager.isLogin(socketAddr.getHostString())) {
			ctx.writeAndFlush(ByteBufWebUtil.readFileFromWebapps(ctx.alloc().directBuffer(), "login.html")).addListener(ChannelFutureListener.CLOSE);
			return ;
		}
		
		ctx.fireChannelRead(msg);
	}
}
