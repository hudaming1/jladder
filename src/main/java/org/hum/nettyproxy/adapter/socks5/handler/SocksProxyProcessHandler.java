package org.hum.nettyproxy.adapter.socks5.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.SocksAuthResponse;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksAuthStatus;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdRequestDecoder;
import io.netty.handler.codec.socks.SocksCmdType;
import io.netty.handler.codec.socks.SocksInitResponse;
import io.netty.handler.codec.socks.SocksRequest;

public class SocksProxyProcessHandler extends SimpleChannelInboundHandler<SocksRequest>{

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
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
				System.out.println(sdf.format(new Date()) + "\t\t\t" + req.host() + ":" + req.port());
				ctx.pipeline().addLast(new ServerPipeChannelHandler());
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
