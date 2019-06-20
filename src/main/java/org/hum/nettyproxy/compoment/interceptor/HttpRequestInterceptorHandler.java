package org.hum.nettyproxy.compoment.interceptor;

import java.util.List;
import java.util.Map;

import org.hum.nettyproxy.common.codec.http.HttpRequestDecoder;
import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.compoment.interceptor.model.InterceptorRegx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpRequestInterceptorHandler extends ChannelInboundHandlerAdapter {

	private Map<String, Interceptor> regxMap; // 需要拦截的域名
	
	public HttpRequestInterceptorHandler(Map<String, Interceptor> regxMap) {
		this.regxMap = regxMap;
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	// 1 如果msg不是HTTP协议，则直接放行
    	if (!isHttpProtocol(msg)) {
    		ctx.fireChannelRead(msg);
    		return ;
    	}
    	
    	// 2.将msg解析成HttpRequest
    	HttpRequest httpRequest = decode(msg);
    	
    	// 3.从配置中获取匹配规则，看看httpRequest是否满足匹配。
    	InterceptorRegx interceptorRegx = doInterceptor(NettyProxyContext.getConfig().getInterceptorRegxList(), httpRequest);
    	
    	// 如果没有匹配到规则，则放行不做处理
    	if (interceptorRegx == null || interceptorRegx.getResponseType() == null) {
    		ctx.fireChannelRead(msg);
    		return ;
    	}
    	
    	// TODO 根据interceptorRegx的Response类型做响应处理 
    	
//    	String namespace = "nettyproxy.com"; // 
    	
    	// 2.从http协议中，解析出要访问的域名
//    	Interceptor interceptor = regxMap.get(namespace);
//    	if (interceptor == null) {
//    		ctx.fireChannelRead(msg);
//    		return ;
//    	}
//    	
//    	// 3.如果域名匹配到interceptNamespaces，则进行拦截，
//    	if (interceptor.doIntercept(namespace, ctx, msg)) {
//    		ctx.fireChannelRead(msg);
//    	}
    }
    
    // 可覆盖
    public InterceptorRegx doInterceptor(List<InterceptorRegx> regxList, HttpRequest httpRequest) {
    	// TODO
    	return null;
    }
    
    private boolean isHttpProtocol(Object msg) {
    	// TODO 判断是否是HTTP协议
    	return true;
    }
    
    private HttpRequest decode(Object msg) {
    	//  解析HTTP协议
    	return HttpRequestDecoder.decode((ByteBuf) msg);
    }
    
    public static interface Interceptor {
    	/**
    	 * 对域名进行拦截
    	 * @return true-继续向后出发；false-直接返回
    	 */
    	boolean doIntercept(String namespace, ChannelHandlerContext ctx, Object msg);
    }
}
