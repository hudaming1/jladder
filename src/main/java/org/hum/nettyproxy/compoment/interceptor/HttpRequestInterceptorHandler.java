package org.hum.nettyproxy.compoment.interceptor;

import java.util.List;

import org.hum.nettyproxy.common.helper.ByteBufHttpHelper;
import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class HttpRequestInterceptorHandler extends ChannelInboundHandlerAdapter {
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	// 1 如果msg不是HTTP协议，则直接放行
    	if (!ByteBufHttpHelper.isHttpProtocol(msg)) {
    		ctx.fireChannelRead(msg);
    		return ;
    	}

		if (msg instanceof ByteBuf) {
			// 2.将msg解析成HttpRequest
			HttpRequest httpRequest = ByteBufHttpHelper.decode((ByteBuf) msg);

			List<InterceptorWrapper> wrappers = InterceptorContext.getWrappers();

			for (InterceptorWrapper wrapper : wrappers) {
				if (wrapper.tryIntercept(httpRequest)) {
					// 拦截成功
					wrapper.doProcess(ctx, httpRequest);
					return;
				}
			}
		}
    	
    	// 没有命中，对请求放行处理
    	ctx.fireChannelRead(msg);
    }
}
