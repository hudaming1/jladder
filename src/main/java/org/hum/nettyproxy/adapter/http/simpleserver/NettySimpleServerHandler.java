package org.hum.nettyproxy.adapter.http.simpleserver;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
public class NettySimpleServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		System.out.println(msg.getHeaders());
	}

}
