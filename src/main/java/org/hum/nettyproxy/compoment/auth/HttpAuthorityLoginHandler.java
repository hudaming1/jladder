package org.hum.nettyproxy.compoment.auth;

import java.net.InetSocketAddress;
import java.util.Map;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpAuthorityLoginHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HttpAuthorityLoginHandler.class);
	private static final String SUBMIT_LOGIN_URI = "/login";
	private AuthManager authManager;
	
	public HttpAuthorityLoginHandler(AuthManager authManager) {
		this.authManager = authManager;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().remoteAddress(); 
		logger.info("auth addr=" + socketAddr.getHostString());

		HttpRequest httpReq = null;
		if (msg instanceof HttpRequest) {
			httpReq = (HttpRequest) msg;
		} else {
			httpReq = ByteBufHttpHelper.decode((ByteBuf) msg);
		}
		
		// 如果是登录请求，则优先处理 
		if (httpReq.getUri().contains(SUBMIT_LOGIN_URI)) {
			Map<String, String> params = HttpUtil.parseBody2FormData(httpReq.getBody());
			if (!authManager.login(socketAddr.getHostString(), params.get("name"), params.get("pass"))) {
				// 登录失败，跳转到403页面，提示无权访问
				ctx.writeAndFlush(ByteBufHttpHelper.readFileFromWebapps(ctx.alloc().directBuffer(), "403.html")).addListener(ChannelFutureListener.CLOSE);
				return ;
			}
			// 登录成功
			String indexUrl = NettyProxyContext.getConfig().getBindHttpServerUrl() + "/index.html";
			ctx.channel().writeAndFlush(ByteBufHttpHelper.create302Response(ctx, indexUrl)).addListener(ChannelFutureListener.CLOSE);
			return ;
		}
		
		ctx.fireChannelRead(msg);
	}
}
