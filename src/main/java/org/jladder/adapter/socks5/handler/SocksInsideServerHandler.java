package org.jladder.adapter.socks5.handler;

import java.util.concurrent.atomic.AtomicInteger;

import org.jladder.adapter.protocol.executor.JladderForwardExecutor;
import org.jladder.adapter.protocol.listener.JladderForwardListener;
import org.jladder.adapter.protocol.message.JladderDataMessage;
import org.jladder.adapter.protocol.message.JladderMessageBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocksInsideServerHandler extends SimpleChannelInboundHandler<SocksCmdRequest> {

	private static final JladderForwardExecutor JladderForwardExecutor = new JladderForwardExecutor();
	private static final AtomicInteger IdCenter = new AtomicInteger(1);
	private static final AtomicInteger FDCounter = new AtomicInteger(0);
	private volatile String clientIden;
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		clientIden = "FD-" + FDCounter.incrementAndGet();
		log.debug(ctx.channel() + " connected");
    }
	
	@Override
	protected void channelRead0(final ChannelHandlerContext browserCtx, final SocksCmdRequest msg) throws Exception {

		if (msg.host() == null || msg.host().isEmpty()) {
			browserCtx.close();
			return;
		}

		browserCtx.pipeline().remove(this);
		browserCtx.pipeline().addLast(new SocksHandler(clientIden, browserCtx, msg));
		browserCtx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, SocksAddressType.IPv4));
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
    }
	
	private static class SocksHandler extends ChannelInboundHandlerAdapter {
		
		private ChannelHandlerContext browserCtx;
		private SocksCmdRequest req;
		private String clientIden;
		
		public SocksHandler(String clientIden, ChannelHandlerContext browserCtx, SocksCmdRequest req) {
			this.clientIden = clientIden;
			this.browserCtx = browserCtx;
			this.req = req;
		}

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        ctx.fireExceptionCaught(cause);
	        if (ctx.channel().isActive()) {
	        	ctx.channel().close();
	        }
	    }

		@Override
	    public void channelRead(ChannelHandlerContext outsideProxyCtx, Object msg) throws Exception {
			JladderDataMessage message = JladderMessageBuilder.buildNeedEncryptMessage(IdCenter.getAndIncrement(), clientIden, req.host(), req.port(), (ByteBuf) msg);
			log.debug("[msg" + message.getMsgId() + "][" + clientIden + "] flush to outside, host=" + req.host() + ", msgLen=" + message.getBody().readableBytes());
			JladderForwardListener listener = JladderForwardExecutor.writeAndFlush(message);
			listener.onReceive(receiveByteBuf -> {
				browserCtx.writeAndFlush(receiveByteBuf.toByteBuf());
			}).onDisconnect(ctx -> {
				browserCtx.close();
				log.debug("channel " + clientIden + " disconnect");
			});
		}
	}
}
