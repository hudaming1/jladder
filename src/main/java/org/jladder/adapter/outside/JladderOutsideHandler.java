package org.jladder.adapter.outside;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jladder.core.JladderAsynForwardClient;
import org.jladder.core.JladderByteBuf;
import org.jladder.core.JladderChannelHandlerContext;
import org.jladder.core.listener.SimpleJladderAsynForwardClientListener;
import org.jladder.core.message.JladderDataMessage;
import org.jladder.core.message.JladderDisconnectMessage;
import org.jladder.core.message.JladderMessage;
import org.jladder.core.message.JladderMessageBuilder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JladderOutsideHandler extends SimpleChannelInboundHandler<JladderMessage> {

	private static final AtomicInteger IdCenter = new AtomicInteger(1);
	private static final EventLoopGroup HttpClientEventLoopGroup = new NioEventLoopGroup(8);
	private static final Map<String, JladderAsynForwardClient> ClientMap = new ConcurrentHashMap<>();
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage jladderMessage) throws Exception {
		String forwardClientKey = jladderMessage.getClientIden() + "#" + jladderMessage.getHost() + ":" + jladderMessage.getPort();
		if (jladderMessage instanceof JladderDataMessage) {
			JladderDataMessage msg = (JladderDataMessage) jladderMessage;
			log.debug("[msg" + jladderMessage.getMsgId() + "][" + forwardClientKey + "] read-len="  + msg.getBody().readableBytes());
			if (!ClientMap.containsKey(forwardClientKey)) {
				// XXX 这里为什么不能用insideCtx的eventLoop(使用ctx.channel().eventLoop()为什么会无响应，哪里有阻塞吗？)
				ClientMap.putIfAbsent(forwardClientKey, new JladderAsynForwardClient(forwardClientKey, msg.getHost(), msg.getPort(), HttpClientEventLoopGroup, new SimpleJladderAsynForwardClientListener() {
					@Override
					public void onReceiveData(JladderByteBuf jladderByteBuf) {
						if (!ClientMap.containsKey(forwardClientKey)) {
							log.info(forwardClientKey + " has closed...");
							return ;
						}
						JladderDataMessage outMsg = msg.isBodyNeedEncrypt() ? JladderMessageBuilder.buildNeedEncryptMessage(IdCenter.getAndIncrement(), msg.getClientIden(), "", 0, jladderByteBuf.toByteBuf()) : JladderMessageBuilder.buildUnNeedEncryptMessage(IdCenter.getAndIncrement(), msg.getClientIden(), "", 0, jladderByteBuf.toByteBuf());
						log.debug("[msg" + outMsg.getMsgId() + "]" + msg.getClientIden() + " flush-len=" + jladderByteBuf.toByteBuf().readableBytes());
						insideCtx.writeAndFlush(outMsg).addListener(f -> {
							if (jladderByteBuf.toByteBuf().refCnt() > 0) {
								jladderByteBuf.toByteBuf().release();
								log.info("release bytebuf");
							}
						});
					}
					@Override
					public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
						insideCtx.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(IdCenter.getAndIncrement(), msg.getClientIden()));
						ClientMap.remove(forwardClientKey);
						log.debug("remote " + forwardClientKey + " disconnect by remote_server");
					}
				}));
			}
			ClientMap.get(forwardClientKey).writeAndFlush(msg.getBody());
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
