package org.hum.nettyproxy.compoment.interceptor;

import java.util.List;

import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.util.HttpUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class HttpRequestInterceptorHandler extends ChannelInboundHandlerAdapter {
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	// 1 如果msg不是HTTP协议，则直接放行
    	if (!isHttpProtocol(msg)) {
    		ctx.fireChannelRead(msg);
    		return ;
    	}
    	
    	// 2.将msg解析成HttpRequest
    	HttpRequest httpRequest = decode(msg);
    	
    	List<InterceptorWrapper> wrappers = InterceptorContext.getWrappers();
    	
    	for (InterceptorWrapper wrapper : wrappers) {
    		if (wrapper.tryIntercept(httpRequest)) {
    			// 拦截成功
    			wrapper.doProcess(ctx, httpRequest);
    			return ;
    		}
    	}
    	
    	// 没有命中，对请求放行处理
    	ctx.fireChannelRead(msg);
    }

    private boolean isHttpProtocol(Object msg) {
    	if (!(msg instanceof ByteBuf)) {
    		return false;
    	}
    	// TODO 判断是否是HTTP协议（目前都是，后续支持socks协议时，这里再完善吧）
    	return true;
    }
    
    private HttpRequest decode(Object msg) {
    	//  解析HTTP协议
    	return HttpUtil.decode((ByteBuf) msg);
    }
    
}
