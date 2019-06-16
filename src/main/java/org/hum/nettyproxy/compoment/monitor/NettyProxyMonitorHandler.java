package org.hum.nettyproxy.compoment.monitor;

import org.hum.nettyproxy.common.core.NettyProxyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * <pre>
 *  NettyProxyMonitiorHandler在进行监控时，总是会在ThreadLocal中获取NettyProxyMonitorManager，
 *  因此再使用NettyProxyMonitiorHandler时，一定要记得在NettyProxyContext注册manager。
 * </pre>
 * @author hudaming
 */
public class NettyProxyMonitorHandler extends ChannelDuplexHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(NettyProxyMonitorHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	// print log
    	logger.info("connected {} <-> {}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
    	// increase connection count
    	NettyProxyMonitorManager monitor = NettyProxyContext.getMonitor();
    	if (monitor != null) {
    		monitor.increaseConnCount();
    	}
        ctx.fireChannelRegistered();
    }
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof ByteBuf) {
	    	ByteBuf byteBuf = (ByteBuf) msg;
	    	// 记录已读取字节数
	    	// increase connection count
	    	NettyProxyMonitorManager monitor = NettyProxyContext.getMonitor();
	    	if (monitor != null) {
	    		monitor.increaseInBytesLength(byteBuf.readableBytes());
	    	}
    	}
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	// print log
    	logger.info("disconnected {} <-> {}", ctx.channel().localAddress(), ctx.channel().remoteAddress());
    	// decrease connection count
    	NettyProxyMonitorManager monitor = NettyProxyContext.getMonitor();
    	if (monitor != null) {
    		monitor.decreaseConnCount();
    	}
        ctx.fireChannelInactive();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if (msg instanceof ByteBuf) {
	    	// 记录输出字节数
	    	ByteBuf byteBuf = (ByteBuf) msg;
	    	NettyProxyMonitorManager monitor = NettyProxyContext.getMonitor();
	    	if (monitor != null) {
	    		monitor.increaseOutBytesLength(byteBuf.writerIndex());
	    	}
    	}
        ctx.write(msg, promise);
    }
}
