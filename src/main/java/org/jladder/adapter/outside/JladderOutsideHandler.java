package org.jladder.adapter.outside;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
public class JladderOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	private static final EventLoopGroup HttpClientEventLoopGroup = new NioEventLoopGroup(8);
	private static final Map<String, JladderAsynForwardClient> ClientMap = new ConcurrentHashMap<>();
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage jladderMessage) throws Exception {
		String forwardClientKey = jladderMessage.getClientIden() + "#" + jladderMessage.getHost() + ":" + jladderMessage.getPort();
		if (jladderMessage instanceof JladderDataMessage) {
			JladderDataMessage msg = (JladderDataMessage) jladderMessage;
			log.info(forwardClientKey + " join..."  + msg.getBody().readableBytes());
			if (!ClientMap.containsKey(forwardClientKey)) {
				// XXX 这里为什么不能用insideCtx的eventLoop(使用ctx.channel().eventLoop()为什么会无响应，哪里有阻塞吗？)
				ClientMap.putIfAbsent(forwardClientKey, new JladderAsynForwardClient(forwardClientKey, msg.getHost(), msg.getPort(), HttpClientEventLoopGroup, new SimpleJladderAsynForwardClientListener() {
					@Override
					public void onReceiveData(JladderByteBuf jladderByteBuf) {
						log.info(msg.getClientIden() + " flush len=" + jladderByteBuf.toByteBuf().readableBytes());
						insideCtx.writeAndFlush(JladderMessageBuilder.buildNeedEncryptMessage(msg.getClientIden(), "", 0, jladderByteBuf.toByteBuf().retain()));
					}
					@Override
					public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
						insideCtx.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(msg.getClientIden()));
						ClientMap.remove(forwardClientKey);
						log.info("remote " + forwardClientKey + " disconnect by remote_server");
					}
				}));
			}
			ClientMap.get(forwardClientKey).writeAndFlush(msg.getBody().retain());
		} else if (jladderMessage instanceof JladderDisconnectMessage) {
			Iterator<Entry<String, JladderAsynForwardClient>> iterator = ClientMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, JladderAsynForwardClient> clientEntry = iterator.next();
				if (clientEntry.getKey().startsWith(jladderMessage.getClientIden() + "#")) {
					clientEntry.getValue().close();
					log.info("disconnect, clientKey=" + clientEntry.getKey() + " by browser");
					iterator.remove();
				}
			}
		}
	}
}
