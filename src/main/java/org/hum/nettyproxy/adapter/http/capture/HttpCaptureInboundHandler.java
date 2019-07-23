package org.hum.nettyproxy.adapter.http.capture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hum.nettyproxy.common.model.HttpRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpResponseDecoder;

@Sharable
public class HttpCaptureInboundHandler extends ChannelDuplexHandler {
	
	private final ThreadLocal<HttpRequest> RequestVar = new ThreadLocal<HttpRequest>();
	
	private HttpCapturePrinter httpCapturePrinter;
	private ResponseDecoder decoder = new ResponseDecoder();
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

        	ByteBuf byteBuf = (ByteBuf) msg;
        	/**
        	 * 1.这里我没有自己实现Response的解码，而是直接套用了Netty自带组件
        	 * 2.关于解码返回，有2种情况（目前这么设计原因还不清楚，为什么有的响应只有HttpContent）
        	 * 3.关于Netty在解码HttpResponse时，只是针对行、和头做了解析，响应内容仍然存在byteBuf中，因此需要打印响应内容，需要自行解析byteBuf
        	 */
        	Object decodeObj = decoder._decode(byteBuf);
        	if (decodeObj instanceof DefaultHttpResponse) {
        		DefaultHttpResponse resp = (DefaultHttpResponse) decodeObj;
        		System.out.println(resp.toString() + "\n   " + byteBuf.toString(io.netty.util.CharsetUtil.UTF_8));
        	}
        	if (decodeObj instanceof DefaultLastHttpContent) {
        		DefaultLastHttpContent resp = (DefaultLastHttpContent) decodeObj;
        		ByteBuf buf = resp.content();
        		System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
        	}
        	byteBuf.resetReaderIndex();
        	
	    	ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					// httpCapturePrinter.flush(httpRequest, response);
				}
	    	});
    		RequestVar.remove();
    	}
    	
        ctx.write(msg, promise);
    }
    
    public class ResponseDecoder extends HttpResponseDecoder {
    	
    	public Object _decode(ByteBuf byteBuf) throws Exception {
    		List<Object> list = new ArrayList<Object>();
    		super.decode(null, byteBuf, list);
    		return list.get(0);
    	}
    }
}
