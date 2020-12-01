package org.jladder.adapter.outside;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jladder.adapter.protocol.JladderAsynForwardClient;
import org.jladder.adapter.protocol.JladderByteBuf;
import org.jladder.adapter.protocol.JladderChannelHandlerContext;
import org.jladder.adapter.protocol.JladderMessage;
import org.jladder.adapter.protocol.listener.SimpleJladderAsynForwardClientListener;

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
	private static final Map<String, JladderAsynForwardClient> ClientMap = new ConcurrentHashMap<>();
	
	@Override
	protected void channelRead0(ChannelHandlerContext insideCtx, JladderMessage msg) throws Exception {
		msg.getBody().retain();
		log.info("[request]" + msg.getClientIden() + "=" + msg.getBody().readableBytes());
		String clientKey = msg.getClientIden();
		JladderAsynForwardClient client = null;
		if (!ClientMap.containsKey(clientKey)) {
			// XXX 这里为什么不能用insideCtx的eventLoop(使用ctx.channel().eventLoop()为什么会无响应，哪里有阻塞吗？)
			client = ClientMap.putIfAbsent(clientKey, new JladderAsynForwardClient(msg.getHost(), msg.getPort(), HttpClientEventLoopGroup, new SimpleJladderAsynForwardClientListener() {
				@Override
				public void onReceiveData(JladderByteBuf jladderByteBuf) {
					System.out.println("receive datas=" + jladderByteBuf.readableBytes());
					insideCtx.writeAndFlush(JladderMessage.buildNeedEncryptMessage(msg.getClientIden(), "", 0, jladderByteBuf.toByteBuf().retain()));
				}
				@Override
				public void onDisconnect(JladderChannelHandlerContext jladderChannelHandlerContext) {
					// 告知断开客户端连接(remote在onclose时，告诉也要断开inside浏览器的连接)
					// insideCtx.writeAndFlush(JladderMessage.buildDisconnectMessage(msg.getClientIden()));
					String removeKey = clientKey;
					ClientMap.remove(removeKey);
					log.info("remote disconnect");
				}
			}));
		}
		client = ClientMap.get(clientKey);
		client.writeAndFlush(msg.getBody());
	}
}
