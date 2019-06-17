package org.hum.nettyproxy.adapter.http.simpleserver;

import java.io.File;

import org.hum.nettyproxy.adapter.http.simpleserver.enumtype.RequestTypeEnum;
import org.hum.nettyproxy.common.util.ByteBufHelper;
import org.hum.nettyproxy.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 首页：
 * 	login.html - 登录页面
 * 	console.html - proxy控制台（当前模式，启动参数，查看监控，限制黑白名单，查看当前登录IP等）
 * 抓包：
 * 	pri_cert.html - 下载私钥证书
 * 	capture.html - 抓包页面
 * PostMan页面：
 * 	postman.html - 模拟postman
 * 
 * @author hudaming
 */
public class NettySimpleServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger logger = LoggerFactory.getLogger(NettySimpleServerHandler.class);
	private static String WEB_ROOT;
	
	public NettySimpleServerHandler() {
		try {
			WEB_ROOT = NettySimpleServerHandler.class.getClassLoader().getResource("").toURI().getPath();
			WEB_ROOT += "webapps";
		} catch (Exception e) {
			WEB_ROOT = "";
			logger.error("init netty-simple-http-server error, can't init web-root-path", e);
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		
		// 1.定位文件
		File file = new File(WEB_ROOT + msg.uri());
		if (!file.exists()) {
			// 返回404页面
			ByteBuf byteBuf = ByteBufHelper.readFile(ctx.alloc().directBuffer(), new File(WEB_ROOT + "/404.html"));
			writeAndFlush(ctx, HttpResponseStatus.NOT_FOUND, byteBuf);
			return ;
		}
		
		// 2.判断文件类型，为response头做准备
		RequestTypeEnum requestType = RequestTypeEnum.get(StringUtil.subHttpUriSuffix(msg.uri()));
		
		// 3.读取文件内容
		ByteBuf byteBuf = ByteBufHelper.readFile(ctx.alloc().directBuffer(), file);
		
		// 4.根据步骤2-3，拼Response
		writeAndFlush(ctx, HttpResponseStatus.OK, requestType, byteBuf);
	}
	
	private void writeAndFlush(ChannelHandlerContext ctx, HttpResponseStatus status, ByteBuf byteBuf) {
		writeAndFlush(ctx, status, RequestTypeEnum.HTML, byteBuf);
	}
	
	private void writeAndFlush(ChannelHandlerContext ctx, HttpResponseStatus status, RequestTypeEnum requestType, ByteBuf byteBuf) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
		if (requestType != null) {
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, requestType.getContentType() + "; charset=UTF-8");
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
