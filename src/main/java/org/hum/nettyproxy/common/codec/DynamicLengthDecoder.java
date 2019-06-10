package org.hum.nettyproxy.common.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 动态长度解码器
 * <pre>
 *    与Netty自带的FixedLengthDecoder不同的是，<code>DynamicLengthDecoder</code>会
 *    先读一个int，作为拆包长度。 
 * </pre>
 * @author hudaming
 */
// TODO 可以加Sharable吗
// @Sharable
public class DynamicLengthDecoder extends ByteToMessageDecoder {

    public DynamicLengthDecoder() {
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    	if (in.readableBytes() < 4) {
    		return null;
    	}
    	int frameLength = in.getInt(0);
        if (in.readableBytes() < frameLength + 4) {
        	System.out.println("readableSize=" + in.readableBytes() + ", frameLength=" + frameLength);
            return null;
        } else {
        	in.skipBytes(4);
            return in.readRetainedSlice(frameLength);
        }
    }
}
