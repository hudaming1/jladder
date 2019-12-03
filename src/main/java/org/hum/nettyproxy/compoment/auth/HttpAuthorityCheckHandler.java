package org.hum.nettyproxy.compoment.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.util.HttpUtil;
import org.hum.nettyproxy.common.util.MD5Util;
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

	public static final String NAME = "HTTPAUTHORITYCHECKHANDLER";
	private static final Logger logger = LoggerFactory.getLogger(HttpAuthorityCheckHandler.class);
	private static final String SUBMIT_LOGIN_URI = "/login/submit";
	private AuthManager authManager;
	
	public HttpAuthorityCheckHandler(AuthManager authManager) {
		this.authManager = authManager;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		logger.info("client enter, clientIp=" + ctx.channel().remoteAddress().toString() + ", ServerIp=" + ctx.channel().localAddress().toString());

		// 如果没有登录，且还不是http协议，则直接让其跳转
		if (!ByteBufHttpHelper.isHttpProtocol(msg)) {
			logger.info("unknow protocol");
			ctx.channel().writeAndFlush(ByteBufHttpHelper.create307Response(ctx.alloc().directBuffer(), "/login.html")).addListener(ChannelFutureListener.CLOSE);
			return ;
		}

		HttpRequest httpReq = null;
		if (msg instanceof HttpRequest) {
			httpReq = (HttpRequest) msg;
		} else {
			httpReq = ByteBufHttpHelper.decode((ByteBuf) msg);
		}

		// 如果已经登录，则权限handler可以放行请求
 		if (authManager.isLogin(convert2ClientIden(ctx, httpReq))) {
			ctx.fireChannelRead(msg);
			return ;
		}
		
		// 如果是登录请求，则放行给后面的Handler处理（实际由HttpAuthorityLoginHandler处理）
		if (httpReq.getUri().contains(SUBMIT_LOGIN_URI)) {
			validate(ctx, httpReq);
			return ;
		}
		
		// 如果没有登录的，但请求URL在白名单中，则也放行
		if (authManager.isUrlInWhilteList(httpReq.toUrl())) {
 			logger.info("url in white_list, url=" + httpReq.toUrl());
			ctx.fireChannelRead(msg);
			return ;
		}

		ByteBuf byteBuf = ctx.alloc().directBuffer();
		byteBuf.writeBytes(ByteBufHttpHelper.readFile2String(new File(ByteBufHttpHelper.getWebRoot() + HttpUtil.parse2RelativeFile("/login.html"))).getBytes());
		// 走到这里的请求，是既没有登录，也是没有在白名单中，则重定向到登录页面
		ctx.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
		logger.info("please login, url=" + httpReq.toUrl());
	}
	
	private void validate(ChannelHandlerContext ctx, HttpRequest request) throws FileNotFoundException, IOException {
		// 1.获得登录参数
		Map<String, String> formData = HttpUtil.parseBody2FormData(request.getBody());
		String userIden = convert2ClientIden(ctx, request);
		
		if (userIden == null) {
			// 返回403 告知no-user-agent
			logger.warn("no user-agent");
		}
		
		// 2.尝试登录
		if (authManager.login(userIden, formData.get("name"), formData.get("password"))) {
			// 登录成功 -> 调到成功页
			// 
			logger.info("login success, userIden=" + userIden);
			ByteBuf byteBuf = ctx.alloc().directBuffer();
			byteBuf.writeBytes(ByteBufHttpHelper.readFile2String(new File(ByteBufHttpHelper.getWebRoot() + HttpUtil.parse2RelativeFile("/success.html"))).getBytes());
			ctx.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);			
		} else {
			// 返回 HTTP 401
			logger.warn("account or passsword invaild");
		}
	}
	
	private String convert2ClientIden(ChannelHandlerContext ctx, HttpRequest request) {
		String userAgent = request.getHeaders().get("user-agent");
		if (userAgent == null || userAgent.isEmpty()) {
			return null;
		}
		return MD5Util.MD5(ctx.channel().remoteAddress().toString() + "@" + userAgent);
	}
}