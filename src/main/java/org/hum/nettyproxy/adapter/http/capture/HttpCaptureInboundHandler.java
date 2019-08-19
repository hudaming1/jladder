package org.hum.nettyproxy.adapter.http.capture;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hum.nettyproxy.common.codec.http.HttpResponseDecoder;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.hum.nettyproxy.common.model.HttpResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

@Sharable
public class HttpCaptureInboundHandler extends ChannelDuplexHandler {
	
	private final ThreadLocal<HttpRequest> RequestVar = new ThreadLocal<HttpRequest>();
	
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
    	HttpRequest httpRequest = RequestVar.get();
    	
    	if (httpRequest != null && msg instanceof ByteBuf) {
    		// 这里的byteBuf可能存在并发，因此先确保同步copy出一个byteBufCopy
        	ByteBuf byteBufCopy = ((ByteBuf) msg).copy();
	    	ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
		        	HttpResponse response;
					try {
			        	/**
			        	 * 1.这里我没有自己实现Response的解码，而是直接套用了Netty自带组件
			        	 * 2.关于解码返回，有2种情况（目前这么设计原因还不清楚，为什么有的响应只有HttpContent，是因为Chunked原因吗）
			        	 * 3.关于Netty在解码HttpResponse时，只是针对行、和头做了解析，响应内容仍然存在byteBuf中，因此需要打印响应内容，需要自行解析byteBuf
			        	 */
						response = httpResponseDecoder.decode(byteBufCopy);
						httpCapturePrinter.flush(httpRequest, response);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	    	});
    		RequestVar.remove();
    	}
    	
        ctx.write(msg, promise);
    }
    
}
