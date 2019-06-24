package org.hum.nettyproxy.compoment.auth;

import java.net.InetSocketAddress;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * TODO 改造成通用一些的Handler，channelRead0不要直接接HttpRequest参数，改为Object
 * 然后判断是不是ByteBuf，再尝试ByteBuf转HttpRequest，这样就能兼容socks代理了
 * 
 * XXX 授权相关的模块，几乎就没有什么可扩展性：
 *    1.没有抽象
 *    2.好多写死的连接（白名单，登录连接判断不严谨，）
 * @author huming
 */
@Sharable
public class HttpAuthorityCheckHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HttpAuthorityCheckHandler.class);
	private AuthManager authManager;
	
	public HttpAuthorityCheckHandler(AuthManager authManager) {
		this.authManager = authManager;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().remoteAddress(); 
		logger.info("auth addr=" + socketAddr.getHostString());
		
		// 如果已经登录，则权限handler可以放行请求
 		if (authManager.isLogin(socketAddr.getHostString())) {
			ctx.fireChannelRead(msg);
			return ;
		}

		// 如果没有登录，且还不是http协议，则直接让其跳转
		if (!ByteBufHttpHelper.isHttpProtocol(msg)) {
			ctx.channel().writeAndFlush(ByteBufHttpHelper.create302Response(ctx, NettyProxyContext.getConfig().getBindHttpServerUrl() + "/login.html")).addListener(ChannelFutureListener.CLOSE);
			return ;
		}

		HttpRequest httpReq = null;
		if (msg instanceof HttpRequest) {
			httpReq = (HttpRequest) msg;
		} else {
			httpReq = ByteBufHttpHelper.decode((ByteBuf) msg);
		}
		
		// 如果没有登录的，但请求URL在白名单中，则也放行
		if (authManager.isUrlInWhilteList(httpReq.toUrl())) {
			ctx.fireChannelRead(msg);
			return ;
		}
		
		// 走到这里的请求，是既没有登录，也是没有在白名单中，则重定向到登录页面
		ctx.channel().writeAndFlush(ByteBufHttpHelper.create302Response(ctx, NettyProxyContext.getConfig().getBindHttpServerUrl() + "/login.html")).addListener(ChannelFutureListener.CLOSE);
	}
}