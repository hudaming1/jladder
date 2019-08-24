package org.hum.nettyproxy.adapter.http.capture;

import java.net.MalformedURLException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hum.nettyproxy.common.codec.http.HttpResponseDecoder;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

@Sharable
public class HttpCaptureInboundHandler extends ChannelDuplexHandler {
	
	private final ThreadLocal<HttpRequest> RequestVar = new ThreadLocal<HttpRequest>();
	
	private final Logger logger = LoggerFactory.getLogger(HttpCaptureInboundHandler.class);
	private HttpCapturePrinter httpCapturePrinter;
	private static final HttpResponseDecoder httpResponseDecoder = new HttpResponseDecoder();
	private static final ThreadPoolExecutor ThreadPool = new ThreadPoolExecutor(1, 4, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(10000)); // 抓个包还能挤压1W吗
	
	public HttpCaptureInboundHandler(HttpCapturePrinter httpCapturePrinter) {
		this.httpCapturePrinter = httpCapturePrinter;
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof HttpRequest) {
    		RequestVar.set((HttpRequest) msg);
    		ctx.fireChannelRead(msg);
    		return ;
    	} 
    	// 非http请求，直接放行也可
        ctx.fireChannelRead(msg);
    }
    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	System.out.println(msg);
    	
    	HttpRequest httpRequest = RequestVar.get();
    	
    	if (httpRequest != null && msg instanceof ByteBuf) {
    		// 关于copy的原因：在decode时，可能会对ByteBuf内部的数据就行修改，为了保证Capture不会影响Proxy正常输出，因此Capture一切操作都给予ByteBuf的副本进行。
        	ByteBuf byteBufCopy = ((ByteBuf) msg).copy();
	    	ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
			        	/**
			        	 * 1.这里我没有自己实现Response的解码，而是直接套用了Netty自带组件
			        	 * 2.关于解码返回，有2种情况（目前这么设计原因还不清楚，为什么有的响应只有HttpContent，是因为Chunked原因吗）
			        	 * 3.关于Netty在解码HttpResponse时，只是针对行、和头做了解析，响应内容仍然存在byteBuf中，因此需要打印响应内容，需要自行解析byteBuf
			        	 */
						 httpCapturePrinter.flush(httpRequest, httpResponseDecoder.decode(byteBufCopy));
					} catch (Exception e) {
						try {
							logger.error("url=" + httpRequest.toUrl().toString(), e);
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					}
				}
	    	});
    		RequestVar.remove();
    	}
    	
        ctx.write(msg, promise);
    }
}
