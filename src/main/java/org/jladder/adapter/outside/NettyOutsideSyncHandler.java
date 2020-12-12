package org.jladder.adapter.outside;

import org.jladder.adapter.protocol.message.JladderDataMessage;
import org.jladder.adapter.protocol.message.JladderDisconnectMessage;
import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.adapter.protocol.message.JladderMessageBuilder;
import org.jladder.adapter.protocol.serial.SimpleJladderSerialization;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyOutsideSyncHandler extends SimpleChannelInboundHandler<JladderMessage> {

	private static final EventLoopGroup EventLoopGroup = new NioEventLoopGroup(16);
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage jladderMessage) throws Exception {
		if (jladderMessage instanceof JladderDataMessage) {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.group(EventLoopGroup);
			bootstrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new SimpleForwardHandler(insideCtx.channel(), jladderMessage.getClientIden()));
				}
			});	
			bootstrap.connect(jladderMessage.getHost(), jladderMessage.getPort()).addListener(f -> {
				log.info("connect " + jladderMessage.getHost() + ":" + jladderMessage.getPort() + " success");
				if (!f.isSuccess()) {
					log.error("connect " + jladderMessage.getHost() + ":" + jladderMessage.getPort() + " failed", f.cause());
				}
				ChannelFuture cf = (ChannelFuture) f;
				insideCtx.pipeline().remove(this);
				JladderDataMessage jdm = (JladderDataMessage) jladderMessage;
				cf.channel().writeAndFlush(jdm.getBody());
			});
		} else if (jladderMessage instanceof JladderDisconnectMessage) {
		}
	}
	
	private static class SimpleForwardHandler extends ChannelInboundHandlerAdapter {
		
		private Channel channel;
		private String iden;
		private static final SimpleJladderSerialization Serialization = new SimpleJladderSerialization();
		
		public SimpleForwardHandler(Channel channel, String iden) {
			this.channel = channel;
			this.iden = iden;
		}

	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	ByteBuf b = (ByteBuf) msg;
	    	log.info("b.len=" + b.readableBytes());
	    	JladderDataMessage message = JladderMessageBuilder.buildUnNeedEncryptMessage(iden, "", 0, b);
	        channel.writeAndFlush(Serialization.serial(message)).addListener(f -> {
	        	if (!f.isSuccess()) {
	        		log.error("flush error", f.cause());
	        	} else {
	        		log.info("flush success");
	        	}
	        });
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    		log.error("channel " + iden + " error", cause);
	    }
	}
}