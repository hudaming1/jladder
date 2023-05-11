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
			if (!ClientMap.containsKey(forwardClientKey)) {
				ClientMap.putIfAbsent(forwardClientKey, new JladderAsynForwardClient(forwardClientKey, msg.getHost(), msg.getPort(), HttpClientEventLoopGroup, new SimpleJladderAsynForwardClientListener() {
					@Override
					public void onReceiveData(JladderByteBuf jladderByteBuf) {
						if (!ClientMap.containsKey(forwardClientKey)) {
							return ;
						}
						try {
							JladderDataMessage outMsg = msg.isBodyNeedEncrypt() ? JladderMessageBuilder.buildNeedEncryptMessage(IdCenter.getAndIncrement(), msg.getClientIden(), "", 0, jladderByteBuf.toByteBuf()) : JladderMessageBuilder.buildUnNeedEncryptMessage(IdCenter.getAndIncrement(), msg.getClientIden(), "", 0, jladderByteBuf.toByteBuf());
							// ⑨ outside接到了对端服务器的响应数据，将ByteBuf加上JladderHeader，封装成新的消息
							// 8. outside接到了对端服务器的响应数据，将ByteBuf加上JladderHeader，封装成新的消息
							log.debug("⑨/8 [" + jladderMessage.getClientIden() + "][" + jladderMessage.getMsgId() + "] outside接到了对端服务器的响应数据，将ByteBuf加上JladderHeader，封装成新的消息");
							insideCtx.writeAndFlush(outMsg);
						} catch (Exception ce) {
							log.error(forwardClientKey, ce);
						}
					}
					@Override
					public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
						insideCtx.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(IdCenter.getAndIncrement(), msg.getClientIden()));
						ClientMap.remove(forwardClientKey);
					}
				}));
			}
			
			// ⑦ outside将JladderMessage发送给远端目标服务器
			// 6. outside将JladderMessage发送给远端服务器
			log.debug("⑦/6[" + msg.getClientIden() + "][" + msg.getMsgId() + "]将消息转发给远端，可发送字节长度=" + msg.getBody().readableBytes());
			
			ClientMap.get(forwardClientKey).writeAndFlush(msg.getBody());
		} else if (jladderMessage instanceof JladderDisconnectMessage) {
			Iterator<Entry<String, JladderAsynForwardClient>> iterator = ClientMap.entrySet().iterator();
			// 按照inside客户端连接维度cloes，而非连接+host维度关闭
			while (iterator.hasNext()) {
				Entry<String, JladderAsynForwardClient> clientEntry = iterator.next();
				if (clientEntry.getKey().startsWith(jladderMessage.getClientIden() + "#")) {
					clientEntry.getValue().close();
					iterator.remove();
				}
			}
		}
	}
}
