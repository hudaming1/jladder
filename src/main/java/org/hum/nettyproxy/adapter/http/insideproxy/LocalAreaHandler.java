package org.hum.nettyproxy.adapter.http.insideproxy;

import org.hum.nettyproxy.adapter.http.simpleproxy.HttpProxyProcessHandler;
import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 局域网转发
 * <pre>
 *    目前没能识别出局域网，只是本地
 * </pre>
 * @author hudaming
 */
@Sharable
public class LocalAreaHandler extends SimpleChannelInboundHandler<HttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		// 如果目标机器在局域网内，则不要转发到outside了，那样会找不到的。。。
		if ("127.0.0.1".equals(msg.getHost())) {
			ctx.pipeline().addLast(new HttpProxyProcessHandler());
			ctx.pipeline().remove(HttpProxyEncryptHandler.class);
		}
		ctx.fireChannelRead(msg);
	}
}
