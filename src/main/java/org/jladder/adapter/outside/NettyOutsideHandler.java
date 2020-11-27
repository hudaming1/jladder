package org.jladder.adapter.outside;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jladder.adapter.protocol.JladderAsynHttpClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderMessage;
import org.jladder.adapter.protocol.JladderMessageReceiveEvent;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class NettyOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	private static final EventLoopGroup HttpClientEventLoopGroup = new NioEventLoopGroup(1);
	private static final Map<String, JladderAsynHttpClient> ClientMap = new ConcurrentHashMap<>();
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage msg) throws Exception {
		msg.getBody().retain();
		log.info("[request]" + msg.getClientIden() + "," + msg.getId() + "=" + msg.getBody().readableBytes());
		String clientKey = msg.getClientIden();
		JladderAsynHttpClient client = null;
		if (!ClientMap.containsKey(clientKey)) {
			// XXX 这里为什么不能用insideCtx的eventLoop(使用ctx.channel().eventLoop()为什么会无响应，哪里有阻塞吗？)
			client = ClientMap.putIfAbsent(clientKey, new JladderAsynHttpClient(msg.getHost(), msg.getPort(), HttpClientEventLoopGroup));
		}
		client = ClientMap.get(clientKey);
		client.writeAndFlush(msg.getBody()).onReceive(new JladderMessageReceiveEvent() {
			@Override
			public void onReceive(JladderByteBuf byteBuf) {
				JladderMessage response = JladderMessage.buildNeedEncryptMessage(msg.getClientIden(), msg.getId(), msg.getHost(), msg.getPort(), byteBuf.toByteBuf().retain());
				log.info("[response]" + response.getId() + "=" + response.getBody().readableBytes());
				insideCtx.writeAndFlush(response);
			}
		});
		
//		TODO
//		client.onClose()
		
//		byte[] bytes = new byte[msg.getBody().readableBytes()];
//		msg.getBody().readBytes(bytes);
//		ByteBuf byteBuf = Unpooled.buffer();
//		String respString = new String(bytes) + " huming";
//		byteBuf.writeBytes(respString.getBytes());
//		insideCtx.writeAndFlush(JladderMessage.buildNeedEncryptMessage(msg.getId(), msg.getHost(), msg.getPort(), byteBuf));
	}
}
