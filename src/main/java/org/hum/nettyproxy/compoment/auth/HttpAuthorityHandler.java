package org.hum.nettyproxy.compoment.auth;

import java.net.InetSocketAddress;
import java.util.Map;

import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.util.HttpUtil;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * TODO 改造成通用一些的Handler，channelRead0不要直接接HttpRequest参数，改为Object
 * 然后判断是不是ByteBuf，再尝试ByteBuf转HttpRequest，这样就能兼容socks代理了
 * @author huming
 */
@Sharable
public class HttpAuthorityHandler extends SimpleChannelInboundHandler<HttpRequest> {
	
	private final AuthManager AuthManager = new AuthManager();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		
		InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().localAddress(); 
		// 如果是登录请求，则优先处理
		if (msg.getUri().contains("/submit_login")) {
			Map<String, String> params = HttpUtil.parseBody2FormData(msg.getBody());
			if (!AuthManager.login(socketAddr.getHostString(), params.get("name"), params.get("pass"))) {
				ctx.writeAndFlush(ByteBufHttpHelper.readFileFromWebapps(ctx.alloc().directBuffer(), "403.html")).addListener(ChannelFutureListener.CLOSE);
				return ;
			}
			ctx.writeAndFlush(ByteBufHttpHelper.readFileFromWebapps(ctx.alloc().directBuffer(), "index.html")).addListener(ChannelFutureListener.CLOSE);
			return ;
		}
		
		// 其他请求，则判断是否已登录
		if (!AuthManager.isLogin(socketAddr.getHostString())) {
			ctx.writeAndFlush(ByteBufHttpHelper.readFileFromWebapps(ctx.alloc().directBuffer(), "login.html")).addListener(ChannelFutureListener.CLOSE);
			return ;
		}
		
		ctx.fireChannelRead(msg);
	}
}
