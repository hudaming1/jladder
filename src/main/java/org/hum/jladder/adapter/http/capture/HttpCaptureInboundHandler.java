package org.hum.jladder.adapter.http.capture;

import java.net.MalformedURLException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hum.jladder.common.codec.http.HttpResponseConverter;
import org.hum.jladder.common.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.LastHttpContent;

@Sharable
public class HttpCaptureInboundHandler extends ChannelDuplexHandler {
	
	private final ThreadLocal<CaptureRecord> CaptureRecordVar = new ThreadLocal<CaptureRecord>();
	
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
    		CaptureRecordVar.set(new CaptureRecord((HttpRequest) msg));
    		ctx.fireChannelRead(msg);
    		return ;
    	} 
    	// 非http请求，直接放行也可
        ctx.fireChannelRead(msg);
    }
    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		CaptureRecord captureRec = CaptureRecordVar.get();
		
		if (msg instanceof HttpResponse || msg instanceof HttpContent) {
        	if (ctx.pipeline().get(HttpResponseEncoder.class) == null) {
        		ctx.pipeline().addBefore("capture", "httpResponseEncoder", new HttpResponseEncoder());
        	}
		}
		
    	if (msg instanceof HttpResponse) {
    		captureRec.setResponse(httpResponseConverter.decode((HttpResponse) msg));
    	} else if (msg instanceof HttpContent) {
    		captureRec.getResponse().getContent().add(((HttpContent) msg).content().copy());
    	}
    	
    	if (msg instanceof LastHttpContent) {
    		ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						 httpCapturePrinter.flush(captureRec.getRequest(), captureRec.getResponse());
					} catch (Exception e) {
						try {
							logger.error("url=" + captureRec.getRequest().toUrl().toString(), e);
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					}
				}
	    	});
    		CaptureRecordVar.remove();
    	}
    	
        ctx.writeAndFlush(msg, promise);
    }
}
