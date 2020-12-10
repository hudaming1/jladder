package org.jladder.adapter.outside;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jladder.adapter.protocol.JladderAsynForwardClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderChannelHandlerContext;
import org.jladder.adapter.protocol.listener.SimpleJladderAsynForwardClientListener;
import org.jladder.adapter.protocol.message.JladderDataMessage;
import org.jladder.adapter.protocol.message.JladderDisconnectMessage;
import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.adapter.protocol.message.JladderMessageBuilder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	private static final EventLoopGroup HttpClientEventLoopGroup = new NioEventLoopGroup(1);
	private static final Map<String, JladderAsynForwardClient> ClientMap = new ConcurrentHashMap<>();
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage jladderMessage) throws Exception {
		String clientKey = jladderMessage.getClientIden() + "-" + jladderMessage.getHost() + ":" + jladderMessage.getPort();
		if (jladderMessage instanceof JladderDataMessage) {
			JladderDataMessage msg = (JladderDataMessage) jladderMessage;
			msg.getBody().retain();
			JladderAsynForwardClient client = null;
			if (!ClientMap.containsKey(clientKey)) {
				// XXX 这里为什么不能用insideCtx的eventLoop(使用ctx.channel().eventLoop()为什么会无响应，哪里有阻塞吗？)
				client = ClientMap.putIfAbsent(clientKey, new JladderAsynForwardClient(msg.getHost(), msg.getPort(), HttpClientEventLoopGroup, new SimpleJladderAsynForwardClientListener() {
					@Override
					public void onReceiveData(JladderByteBuf jladderByteBuf) {
						insideCtx.writeAndFlush(JladderMessageBuilder.buildNeedEncryptMessage(msg.getClientIden(), "", 0, jladderByteBuf.toByteBuf().retain()));
					}
					@Override
					public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
						insideCtx.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(msg.getClientIden()));
						ClientMap.remove(clientKey);
						log.info("remote " + clientKey + " disconnect");
					}
				}));
			}
			client = ClientMap.get(clientKey);
			client.writeAndFlush(msg.getBody());
		} else if (jladderMessage instanceof JladderDisconnectMessage) {
			JladderAsynForwardClient client = ClientMap.get(clientKey);
			if (client != null) {
				log.info("disconnect, clientKey=" + clientKey);
				client.close();
			} else {
				log.warn("disconnect failed, clientKey={}, map={}", clientKey, ClientMap);
			}
		}
	}
}
