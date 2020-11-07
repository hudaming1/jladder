package org.hum.jladder.common.codec.http;

import org.hum.jladder.common.helper.ByteBufHttpHelper;
import org.hum.jladder.common.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * HTTP请求解码器
 * @author hudaming
 */
public class HttpRequestDecoder extends ChannelInboundHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof ByteBuf) {
	    	ByteBuf byteBuf = (ByteBuf) msg;
	    	HttpRequest httpRequest = ByteBufHttpHelper.decode(byteBuf);
	    	if (logger.isDebugEnabled()) {
		    	logger.debug("========================================");
		    	logger.debug(httpRequest.toString());
		    	logger.debug("========================================");
	    	}
	        ctx.fireChannelRead(httpRequest);
    	} else {
    		ctx.fireChannelRead(msg);
    	}
    }
}
