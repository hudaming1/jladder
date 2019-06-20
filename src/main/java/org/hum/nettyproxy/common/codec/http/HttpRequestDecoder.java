package org.hum.nettyproxy.common.codec.http;

import org.hum.nettyproxy.common.util.HttpUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * HTTP请求解码器
 * @author hudaming
 */
public class HttpRequestDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf byteBuf = (ByteBuf) msg;
        ctx.fireChannelRead(HttpUtil.decode(byteBuf));
    }
}
