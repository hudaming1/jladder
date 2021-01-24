package org.jladder.adapter.socks5.handler;

import org.jladder.common.IdCenter;
import org.jladder.core.executor.JladderForwardExecutor;
import org.jladder.core.listener.JladderForwardListener;
import org.jladder.core.message.JladderDataMessage;
import org.jladder.core.message.JladderMessageBuilder;

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
	private volatile String clientIden;
	
	@Override
	protected void channelRead0(final ChannelHandlerContext browserCtx, final SocksCmdRequest msg) throws Exception {

		log.info("read message, host={}, port={}", msg.host(), msg.port());
		if (msg.host() == null || msg.host().isEmpty()) {
			browserCtx.close();
			return;
		}

		clientIden = IdCenter.gen("FD");
		browserCtx.pipeline().remove(this);
		browserCtx.pipeline().addLast(new SocksHandler(browserCtx, msg));
		browserCtx.channel().writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, SocksAddressType.IPv4));
		log.info("flush success-message");
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
    }
	
	private class SocksHandler extends ChannelInboundHandlerAdapter {
		
		private ChannelHandlerContext browserCtx;
		private SocksCmdRequest req;
		
		public SocksHandler(ChannelHandlerContext browserCtx, SocksCmdRequest req) {
			this.browserCtx = browserCtx;
			this.req = req;
		}

		@Override
	    public void channelRead(ChannelHandlerContext outsideProxyCtx, Object msg) throws Exception {
			JladderDataMessage message = JladderMessageBuilder.buildNeedEncryptMessage(System.nanoTime(), clientIden, req.host(), req.port(), (ByteBuf) msg);
			log.info("[msg" + message.getMsgId() + "][" + clientIden + "] flush to outside, host=" + req.host() + ", msgLen=" + message.getBody().readableBytes());
			JladderForwardListener listener = JladderForwardExecutor.writeAndFlush(message);
			listener.onReceive(receiveByteBuf -> {
				log.info("receive message");
				browserCtx.writeAndFlush(receiveByteBuf.toByteBuf());
			}).onDisconnect(ctx -> {
				browserCtx.close();
				log.debug("channel " + clientIden + " disconnect");
			});
		}


	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	log.error(clientIden + " browser error", cause);
	    	if (ctx.channel().isActive()) {
	    		ctx.channel().close();
	    	}
			JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(System.nanoTime(), clientIden));
	    }

	    @Override
	    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			log.debug("channel " + clientIden + " disconnect");
			JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(System.nanoTime(), clientIden));
			JladderForwardExecutor.clearClientIden(clientIden);
	    }
	}
}
