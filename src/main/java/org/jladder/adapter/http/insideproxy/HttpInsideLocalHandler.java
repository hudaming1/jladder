package org.jladder.adapter.http.insideproxy;

import org.jladder.adapter.http.wrapper.HttpRequestWrapper;
import org.jladder.adapter.http.wrapper.HttpRequestWrapperHandler;
import org.jladder.common.Constant;
import org.jladder.common.IdCenter;
import org.jladder.core.executor.JladderForwardExecutor;
import org.jladder.core.listener.JladderForwardListener;
import org.jladder.core.message.JladderDataMessage;
import org.jladder.core.message.JladderMessageBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP/HTTPS 加密转发
 * <pre>
 *   针对HTTP请求，需要程序进行加密解密转发；而针对HTTPS请求，加解密由SSL协议完成，因此只需要透传转发。
 * </pre>
 * @author hudaming
 */
@Slf4j
public class HttpInsideLocalHandler extends SimpleChannelInboundHandler<HttpRequestWrapper> {

	private static final ByteBuf HTTPS_CONNECTED_LINE = PooledByteBufAllocator.DEFAULT.directBuffer();
	private static final JladderForwardExecutor JladderForwardExecutor = new JladderForwardExecutor();
	static {
		HTTPS_CONNECTED_LINE.writeBytes(Constant.ConnectedLine.getBytes());
	}
	
	private volatile String clientIden;
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		clientIden = IdCenter.gen("FD");
		// ① 客户端上线
		log.info("[" + clientIden + "]上线");
    }
    
	@Override
	protected void channelRead0(ChannelHandlerContext browserCtx, HttpRequestWrapper requestWrapper) throws Exception {
		
		if (requestWrapper.host() == null || requestWrapper.host().isEmpty()) {
			browserCtx.close(); 
			log.warn("request is invaild, or version is http/1.0, request=" + requestWrapper.toRequest());
			return;
		}
		
		// 转发前记录真实IP，防止转发中丢失源IP地址
		requestWrapper.header("x-forwarded-for", browserCtx.channel().remoteAddress().toString());
		
		if (requestWrapper.isHttps()) {
			browserCtx.pipeline().remove(this);
			browserCtx.pipeline().remove(io.netty.handler.codec.http.HttpRequestDecoder.class);
			browserCtx.pipeline().remove(HttpObjectAggregator.class);
			browserCtx.pipeline().remove(HttpRequestWrapperHandler.class);
			browserCtx.pipeline().addLast(new SimpleForwardChannelHandler(clientIden, requestWrapper.host(), requestWrapper.port()));
			browserCtx.writeAndFlush(HTTPS_CONNECTED_LINE.retain()).addListener(f -> {
				// ② SSL握手完成，获得客户端还要访问的目标地址
				log.info("[" + clientIden + "]SSL握手完成，对端地址为：" + requestWrapper.host() + ":" + requestWrapper.port()); 
			});
			return ;
		} else {
			JladderDataMessage message = JladderMessageBuilder.buildNeedEncryptMessage(System.nanoTime(), clientIden, requestWrapper.host(), requestWrapper.port(), requestWrapper.toByteBuf());
			log.debug("[msg" + message.getMsgId() + "][" + clientIden + "] flush to outside, host=" + requestWrapper.host() + ", msgLen=" + message.getBody().readableBytes());
			JladderForwardListener listener = JladderForwardExecutor.writeAndFlush(message);
			listener.onReceive(byteBuf -> {
				browserCtx.writeAndFlush(byteBuf.toByteBuf());
				// byteBuf.release();
			}).onDisconnect(ctx -> {
				browserCtx.close();
				log.debug("channel " + clientIden + " disconnect");
			});
		}
	}

    /**
     * 【建立连接阶段】客户端报错
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	log.error(clientIden + " browser error", cause);
    	if (ctx.channel().isActive()) {
    		ctx.channel().close();
    	}
		JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(System.nanoTime(), clientIden));
    }


    /**
     * 【建立连接阶段】客户端主动断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("channel " + clientIden + " disconnect");
		JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(System.nanoTime(), clientIden));
		JladderForwardExecutor.clearClientIden(clientIden);
    }
	
	private static class SimpleForwardChannelHandler extends ChannelInboundHandlerAdapter {
		
		private String clientIden;
		private String remoteHost;
		private int remotePort;
		
		public SimpleForwardChannelHandler(String clientIden, String host, int port) {
			this.clientIden = clientIden;
			this.remoteHost = host;
			this.remotePort = port;
		}

	    @Override
	    public void channelRead(ChannelHandlerContext browserCtx, Object msg) throws Exception {
	    	if (msg instanceof ByteBuf) {
	    		ByteBuf msgByteBuf = (ByteBuf) msg;
	    		int clientMessageLen = msgByteBuf.readableBytes();
	    		JladderDataMessage request = JladderMessageBuilder.buildUnNeedEncryptMessage(System.nanoTime(), clientIden, remoteHost, remotePort, msgByteBuf);
	    		// ③ inside接收到客户端消息，并将byteBuf封装成jladderMessage
	    		log.info("[" + clientIden + "]收到客户端消息长度=" + clientMessageLen + "，封装后的消息Id=" + request.getMsgId());
	    		JladderForwardListener listener = JladderForwardExecutor.writeAndFlush(request);
	    		listener.onReceive(byteBuf -> {
	    			// ⑬ inside接收到outside的JladderMessage类型消息，并将body输出给客户端
	    			log.info("[" + clientIden + "]inside接收到outside的JladderMessage类型消息，并将body输出给客户端，body长度=" + byteBuf.toByteBuf().readableBytes());
	    			browserCtx.writeAndFlush(byteBuf.toByteBuf());
	    		}).onDisconnect(ctx -> {
					browserCtx.close();
					log.info("channel " + clientIden + " disconnect by remote_server");
				});
	    	}
	    }

	    /**
	     * 【传输数据阶段】客户端报错
	     */
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	log.error(clientIden + " proxy error, host=" + remoteHost + ":" + remotePort, cause);
	    	if (ctx.channel().isActive()) {
	    		ctx.channel().close();
	    	}
			JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(System.nanoTime(), clientIden));
	    }

	    /**
	     * 【传输数据阶段】客户度主动断开连接
	     */
	    @Override
	    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	    	log.info("channel " + clientIden + " disconnect by browser");
			JladderForwardExecutor.writeAndFlush(JladderMessageBuilder.buildDisconnectMessage(System.nanoTime(), clientIden));
			JladderForwardExecutor.clearClientIden(clientIden);
	    }
	}
}
