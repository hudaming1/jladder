package org.hum.nettyproxy.compoment.auth;

import java.net.InetSocketAddress;
import java.util.Map;

import org.hum.nettyproxy.adapter.socks5.handler.SocksInsideServerHandler;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.util.HttpUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;
import io.netty.util.ReferenceCountUtil;

/**
 * TODO 改造成通用一些的Handler，channelRead0不要直接接HttpRequest参数，改为Object
 * 然后判断是不是ByteBuf，再尝试ByteBuf转HttpRequest，这样就能兼容socks代理了
 * @author huming
 */
@Sharable
public class HttpAuthorityHandler extends ChannelInboundHandlerAdapter {
	
	private final AuthManager AuthManager = new AuthManager();

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

			InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().localAddress(); 
			
			// 如果已经登录，则权限handler可以放行请求
	 		if (AuthManager.isLogin(socketAddr.getHostString())) {
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
			
			// 如果是登录请求，则优先处理
			if (httpReq.getUri().contains("/submit_login")) {
				Map<String, String> params = HttpUtil.parseBody2FormData(httpReq.getBody());
				if (!AuthManager.login(socketAddr.getHostString(), params.get("name"), params.get("pass"))) {
					// 登录失败，跳转到403页面，提示无权访问
					ctx.writeAndFlush(ByteBufHttpHelper.readFileFromWebapps(ctx.alloc().directBuffer(), "403.html")).addListener(ChannelFutureListener.CLOSE);
					return ;
				}
				// 登录成功
				String indexUrl = NettyProxyContext.getConfig().getBindHttpServerUrl() + "/index.html";
				ctx.channel().writeAndFlush(ByteBufHttpHelper.create302Response(ctx, indexUrl)).addListener(ChannelFutureListener.CLOSE);
				return ;
			}
			
			// 如果没有登录的，但请求URL在白名单中，则也放行
			if (AuthManager.isUrlInWhilteList(httpReq.toUrl())) {
				ctx.fireChannelRead(msg);
				return ;
			}
			
			// 走到这里的请求，是既没有登录，也是没有在白名单中，则重定向到登录页面
			ctx.channel().writeAndFlush(ByteBufHttpHelper.create302Response(ctx, NettyProxyContext.getConfig().getBindHttpServerUrl() + "/login.html")).addListener(ChannelFutureListener.CLOSE);
	}
}

class _302Hanlder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (ByteBufHttpHelper.isHttpProtocol(msg)) {
			HttpRequest httpReq = ByteBufHttpHelper.decode((ByteBuf) msg);
			
			return ;
		}
    	System.out.println("pre flush 302");
		ctx.channel().writeAndFlush(ByteBufHttpHelper.create302Response(ctx, NettyProxyContext.getConfig().getBindHttpServerUrl() + "/login.html")).addListener(ChannelFutureListener.CLOSE);
		System.out.println("flush 302");
    }
}
