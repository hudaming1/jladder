package org.hum.nettyproxy.adapter.http.consoleserver.handler;

import java.net.InetSocketAddress;
import java.util.Map;

import org.hum.nettyproxy.adapter.http.consoleserver.NettyHttpUriHandler;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.util.HttpUtil;
import org.hum.nettyproxy.compoment.auth.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

@Sharable
public class HttpAuthorityLoginHandler extends NettyHttpUriHandler {

	private static final Logger logger = LoggerFactory.getLogger(HttpAuthorityLoginHandler.class);
	private final static String uri = "/login/submit";
	private final AuthManager authManager;
	
	public HttpAuthorityLoginHandler(AuthManager authManager) {
		super(uri);
		this.authManager = authManager;
	}

	@Override
	public void process(ChannelHandlerContext ctx, FullHttpRequest req) {
		try {
			InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().localAddress(); 
			byte[] bytes = new byte[req.content().readableBytes()];
			req.content().readBytes(bytes);
			Map<String, String> params = HttpUtil.parseBody2FormData(new String(bytes));
			if (!authManager.login(socketAddr.getHostString(), params.get("name"), params.get("pass"))) {
				// 登录失败，跳转到403页面，提示无权访问
				ctx.writeAndFlush(ByteBufHttpHelper.readFileFromWebapps(ctx.alloc().directBuffer(), "403.html")).addListener(ChannelFutureListener.CLOSE);
				return ;
			}
			// 登录成功
			String indexUrl = NettyProxyContext.getConfig().getBindHttpServerUrl() + "/index.html";
			ctx.pipeline().firstContext().writeAndFlush(ByteBufHttpHelper.create302Response(ctx, indexUrl)).addListener(ChannelFutureListener.CLOSE);
			return ;
		} catch (Exception ce) {
			ctx.channel().close();
			logger.error("process error", ce);
			return ;
		}
	}
}
