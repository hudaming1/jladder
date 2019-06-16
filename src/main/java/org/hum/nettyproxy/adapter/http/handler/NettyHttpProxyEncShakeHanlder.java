package org.hum.nettyproxy.adapter.http.handler;

import org.hum.nettyproxy.adapter.http.model.HttpRequest;
import org.hum.nettyproxy.common.Constant;
import org.hum.nettyproxy.common.codec.customer.DynamicLengthDecoder;
import org.hum.nettyproxy.common.codec.customer.NettyProxyBuildSuccessMessageCodec.NettyProxyBuildSuccessMessage;
import org.hum.nettyproxy.common.handler.DecryptPipeChannelHandler;
import org.hum.nettyproxy.common.handler.EncryptPipeChannelHandler.Encryptor;
import org.hum.nettyproxy.common.handler.ForwardHandler;
import org.hum.nettyproxy.common.handler.InactiveHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 配合HttpProxyEncryptHandler处理
 * @author hudaming
 */
public class NettyHttpProxyEncShakeHanlder extends ChannelInboundHandlerAdapter {

	private Channel browserChannel;
	private HttpRequest req;
	
	public NettyHttpProxyEncShakeHanlder(Channel channel, HttpRequest req) {
		this.browserChannel = channel;
		this.req = req;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext outsideProxyCtx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg; // msg-value.type = NettyProxyBuildSuccessMessage
        
        // 收到对端的BuildSuccessMessage，说明Proxy已经和目标服务器建立连接成功
        if (byteBuf.readInt() != Constant.MAGIC_NUMBER || byteBuf.readInt() != NettyProxyBuildSuccessMessage.SUCCESS) {
        	outsideProxyCtx.close();
        	browserChannel.close();
        	return ;
        }
        
        /** 正常情况 **/
        // 脱壳(握手成功后，就开始进行加密通信，因此这个handler就没用了)
        outsideProxyCtx.pipeline().remove(this);

        // https：开启双向通信
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

        // TODO 这里我感觉应该可以优化：能不能直接返回byteBuf里的arr，从而不要再次开辟一段新的内存空间。
		byte[] arr = new byte[req.getByteBuf().readableBytes()];
		req.getByteBuf().readBytes(arr);
		
        // 转发给outside_server（HTTP协议因为是明文协议，因此在和Proxy通信时，需要程序自己加密）
        outsideProxyCtx.pipeline().writeAndFlush(Encryptor.encrypt(outsideProxyCtx.alloc().directBuffer(), arr));
    }
}