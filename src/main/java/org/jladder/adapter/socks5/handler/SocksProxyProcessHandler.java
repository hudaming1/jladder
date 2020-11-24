package org.jladder.adapter.socks5.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.socks.SocksAuthResponse;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksAuthStatus;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdRequestDecoder;
import io.netty.handler.codec.socks.SocksCmdType;
import io.netty.handler.codec.socks.SocksInitResponse;
import io.netty.handler.codec.socks.SocksRequest;

@Sharable
public class SocksProxyProcessHandler extends SimpleChannelInboundHandler<SocksRequest>{

	private static final Logger logger = LoggerFactory.getLogger(SocksProxyProcessHandler.class);
	private final SocksInsideServerHandler socksInsideServerHandler = new SocksInsideServerHandler();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SocksRequest msg) throws Exception {
		switch (msg.requestType()) {
		case INIT:
			ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
			ctx.writeAndFlush(new SocksInitResponse(SocksAuthScheme.NO_AUTH));
			break;
		case AUTH:
			ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
			ctx.writeAndFlush(new SocksAuthResponse(SocksAuthStatus.SUCCESS));
			break;
		case CMD:
			SocksCmdRequest req = (SocksCmdRequest) msg;
			if (req.cmdType() == SocksCmdType.CONNECT) {
				logger.info("prepare connect {}:{}", req.host(), req.port());
				ctx.pipeline().addLast(socksInsideServerHandler);
				ctx.pipeline().remove(this);
				ctx.fireChannelRead(msg);
			} else {
				ctx.close();
			}
			break;
		case UNKNOWN:
			ctx.close();
			break;
		default:
			ctx.close();
			break;
		}
	}
}
