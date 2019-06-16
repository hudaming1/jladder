package org.hum.nettyproxy.compoment.interceptor;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NamespaceRobberHandler extends ChannelInboundHandlerAdapter {

	private Map<String, Interceptor> regxMap; // 需要拦截的域名
	
	public NamespaceRobberHandler(Map<String, Interceptor> regxMap) {
		this.regxMap = regxMap;
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	// 1.解析msg，判断是否是HTTP协议
    	String namespace = "nettyproxy.com"; // TODO
    	
    	// 2.从http协议中，解析出要访问的域名
    	Interceptor interceptor = regxMap.get(namespace);
    	if (interceptor == null) {
    		ctx.fireChannelRead(msg);
    		return ;
    	}
    	
    	// 3.如果域名匹配到interceptNamespaces，则进行拦截，
    	if (interceptor.doIntercept(namespace, ctx, msg)) {
    		ctx.fireChannelRead(msg);
    	}
    }
    
    public static interface Interceptor {
    	/**
    	 * 对域名进行拦截
    	 * @return true-继续向后出发；false-直接返回
    	 */
    	boolean doIntercept(String namespace, ChannelHandlerContext ctx, Object msg);
    }
}
