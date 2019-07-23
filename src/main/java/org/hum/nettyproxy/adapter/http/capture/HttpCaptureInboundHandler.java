package org.hum.nettyproxy.adapter.http.capture;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;

@Sharable
public class HttpCaptureInboundHandler extends ChannelDuplexHandler {
	
	private final ThreadLocal<HttpRequest> RequestVar = new ThreadLocal<HttpRequest>();
	
	private HttpCapturePrinter httpCapturePrinter;
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
    	if (httpRequest != null && msg instanceof DefaultFullHttpResponse) {
	    	ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					DefaultFullHttpResponse resp = (DefaultFullHttpResponse) msg;
					System.out.println(resp);
					httpCapturePrinter.flush(httpRequest, null);
				}
	    	});
    		RequestVar.remove();
    	}
        ctx.write(msg, promise);
    }
}
