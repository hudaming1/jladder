package org.hum.nettyproxy.adapter.http.capture;

import java.net.MalformedURLException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hum.nettyproxy.common.codec.http.HttpResponseConverter;
import org.hum.nettyproxy.common.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;

@Sharable
public class HttpCaptureInboundHandler extends ChannelDuplexHandler {
	
	private final ThreadLocal<HttpRequest> RequestVar = new ThreadLocal<HttpRequest>();
	
	private final Logger logger = LoggerFactory.getLogger(HttpCaptureInboundHandler.class);
	private HttpCapturePrinter httpCapturePrinter;
	private static final HttpResponseConverter httpResponseConverter = new HttpResponseConverter();
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
    
    /**
     * 这里接到的msg，你会发现由于chunked响应，会导致一个Response报文，会被拆分成多个Msg传入，因此需要做特殊解析。
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	
    	if (msg instanceof HttpResponse) {
        	HttpRequest httpRequest = RequestVar.get();
    		ctx.pipeline().addBefore("capture", "httpResponseEncoder", new HttpResponseEncoder());
    		ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
			        	/**
			        	 * 1.这里我没有自己实现Response的解码，而是直接套用了Netty自带组件
			        	 * 2.关于解码返回，有2种情况（目前这么设计原因还不清楚，为什么有的响应只有HttpContent，是因为Chunked原因吗）
			        	 * 3.关于Netty在解码HttpResponse时，只是针对行、和头做了解析，响应内容仍然存在byteBuf中，因此需要打印响应内容，需要自行解析byteBuf
			        	 */
						 httpCapturePrinter.flush(httpRequest, httpResponseConverter.decode((HttpResponse) msg));
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
