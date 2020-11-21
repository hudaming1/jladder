package org.hum.jladder.adapter.http.insideproxy;

import org.hum.jladder.adapter.http.wrapper.HttpRequestWrapper;
import org.hum.jladder.adapter.protocol.JladderByteBuf;
import org.hum.jladder.adapter.protocol.JladderChannelFuture;
import org.hum.jladder.adapter.protocol.JladderForward;
import org.hum.jladder.adapter.protocol.listener.JladderConnectListener;
import org.hum.jladder.adapter.protocol.listener.JladderReadListener;
import org.hum.jladder.common.core.NettyProxyContext;
import org.hum.jladder.common.core.config.JladderConfig;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * HTTP/HTTPS 加密转发
 * <pre>
 *   针对HTTP请求，需要程序进行加密解密转发；而针对HTTPS请求，加解密由SSL协议完成，因此只需要透传转发。
 * </pre>
 * @author hudaming
 */
@Sharable
public class HttpInsideLocalHandler extends SimpleChannelInboundHandler<HttpRequestWrapper> {

	private final static JladderConfig Config = NettyProxyContext.getConfig();	
	
	@Override
	protected void channelRead0(ChannelHandlerContext browserCtx, HttpRequestWrapper requestWrapper) throws Exception {

		if (requestWrapper.host() == null || requestWrapper.host().isEmpty()) {
			/**
			 * 这里不要close，否则用Chrome访问news.baidu.com会导致EmptyResponse
			 * 在调试时发现，decode第一个请求正常，但第二个请求则不是一个正常的http请求，此时disscard比close更有利于后面处理
			 */
			// browserCtx.close(); 
			return;
		}
		
		// 转发前记录真实IP，防止转发中丢失源IP地址
		requestWrapper.header("x-forwarded-for", browserCtx.channel().remoteAddress().toString());
		
		if (requestWrapper.isHttps()) {
			
		}
		
		JladderForward forward = new JladderForward(Config.getOutsideProxyHost(), Config.getOutsideProxyPort(), browserCtx.channel().eventLoop());
		forward.connect(requestWrapper.host(), requestWrapper.port(), new JladderConnectListener() {
			@Override
			public void onConnect(JladderChannelFuture future) {
				future.writeAndFlush(requestWrapper.toBytes());
			}
		}).onRead(new JladderReadListener() {
			@Override
			public void onRead(JladderByteBuf msg) {
				browserCtx.writeAndFlush(msg.toByteBuf());
			}
		});
	}
	
}
