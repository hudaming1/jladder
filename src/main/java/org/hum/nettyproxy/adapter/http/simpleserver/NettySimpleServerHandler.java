package org.hum.nettyproxy.adapter.http.simpleserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

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

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		String accessFile = msg.uri();
		
		// 1.定位文件
		
		// 2.判断文件类型，为response头做准备
		
		// 3.读取文件内容
		
		// 4.根据步骤2-3，拼Response
	}
}
