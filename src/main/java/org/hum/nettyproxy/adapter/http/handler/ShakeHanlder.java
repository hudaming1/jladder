package org.hum.nettyproxy.adapter.http.handler;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;
import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.codec.DynamicLengthDecoder;
import org.hum.nettyproxy.common.codec.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.handler.DecryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.ForwardHandler;
import org.hum.nettyproxy.common.handler.InactiveHandler;
import org.hum.nettyproxy.common.util.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ShakeHanlder extends ChannelInboundHandlerAdapter {

	private Channel browserChannel;
	private HttpRequest req;
	
	public ShakeHanlder(Channel channel, HttpRequest req) {
		this.browserChannel = channel;
		this.req = req;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext outsideProxyCtx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg; // msg-value.type = NettyProxyBuildSuccessMessage
        
        // 收到对端的BuildSuccessMessage，说明Proxy已经和目标服务器建立连接成功
        if (byteBuf.readInt() != Constant.MAGIC_NUMBER || byteBuf.readInt() != NettyProxyBuildSuccessMessage.SUCCESS) {
        	System.out.println("proxy connect " + req.getHost() + ":" + req.getPort() + " failure. "); // 告知断开连接
        	outsideProxyCtx.close();
        	browserChannel.close();
        	return ;
        }
        
        /** 正常情况 **/
        // 脱壳(握手成功后，就开始进行加密通信，因此这个handler就没用了)
        outsideProxyCtx.pipeline().remove(this);
        
        // https：开启双向通信
        System.out.println("proxy connect [" + req.getHost() + "] success");
        
        if (req.isHttps()) { 
        	// HTTPS协议只有在第一次握手时用明文，交换秘钥后浏览器和目标服务器自己实现了加密，因此程序只需要透传即可。
        	outsideProxyCtx.pipeline().addLast(new ForwardHandler("outside_server->browser", browserChannel), new InactiveHandler(browserChannel));
        	browserChannel.pipeline().addLast(new ForwardHandler("browser->ouside_server", outsideProxyCtx.channel()));
			// 与服务端建立连接完成后，告知浏览器Connect成功，可以进行ssl通信了
        	browserChannel.writeAndFlush(Unpooled.wrappedBuffer(Constant.ConnectedLine.getBytes())); // TODO 待优化，在direct上分配，而且不用每次都创建，用完改下readIndex就可以
        	return ;
        } 
        
        // proxy.response -> browser (仅开启单项转发就够了，因为HTTP是请求/应答协议)
        outsideProxyCtx.pipeline().addLast(new DynamicLengthDecoder(), new DecryptPipeChannelHandler(browserChannel), new InactiveHandler(browserChannel));
        
        // HTTP协议因为是明文协议，因此在和Proxy通信时，需要程序自己加密
		byte[] arr = new byte[req.getByteBuf().readableBytes()];
		req.getByteBuf().readBytes(arr);
		byte[] encrypt = Utils.encrypt(arr);
        ByteBuf buf = outsideProxyCtx.alloc().directBuffer();
        buf.writeInt(encrypt.length);
        buf.writeBytes(encrypt);
		System.out.println("encode.len=" + encrypt.length);
        
        // 转发给Proxy
        outsideProxyCtx.pipeline().writeAndFlush(buf);
        System.out.println("flush req");
    }
}