package org.jladder.core.executor;

import java.util.Arrays;
import java.util.List;

import org.jladder.core.crypto.CryptoFactory;
import org.jladder.core.enumtype.JladderMessageTypeEnum;
import org.jladder.core.message.JladderMessage;
import org.jladder.core.serial.JladderSerialization;
import org.jladder.core.serial.SimpleJladderSerialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

//public class JladderCryptoInHandler extends ReplayingDecoder<JladderMessage> {
//
//	private final JladderSerialization jladderSerialization = new SimpleJladderSerialization();
//
//	@Override
//	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//		out.add(jladderSerialization.deserial(in));
//	}
//}

public class JladderCryptoInHandler extends ByteToMessageDecoder {

	private final JladderSerialization jladderSerialization = new SimpleJladderSerialization();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {

		if (buf.readableBytes() < 18) {
			System.out.println("unreasdfasdfasdlfjaslkfjlasdjfk");
			return;
		}
		
		if (!isCompleted(buf)) {
			return ;
		}
		
		out.add(jladderSerialization.deserial(buf));
	}
	
	private boolean isCompleted(ByteBuf in) {
		in.markReaderIndex();
		try {
			int readableBytes = in.readableBytes();
			in.skipBytes(8); // skip magic_number
			in.readLong();
			short msgType = in.readShort();
			JladderMessageTypeEnum messageType = JladderMessageTypeEnum.getEnum(msgType);
			if (messageType == JladderMessageTypeEnum.Data) {
				// read client_iden
				int idenLen = in.readInt();
				byte[] idenBytes = new byte[idenLen];
				in.readBytes(idenBytes);
				// read host
				int hostLen = in.readInt();
				byte[] hostBytes4Encrypt = new byte[hostLen];
				in.readBytes(hostBytes4Encrypt);
				// read port
				in.readInt();
				// read body
				in.readBoolean();
				int bodyLen = in.readInt();
				boolean isCompldted = readableBytes >= bodyLen + 31 + idenLen + hostLen;
				System.out.println("JladderCryptoInHandler.java ------> " + in + ", refCnt=" + in.refCnt() + ", readableBytes=" + readableBytes + ", bodyLen=" + bodyLen + ", total=" + (bodyLen + 31 + idenLen + hostLen));
				if (isCompldted) {
					byte[] bbbb = new byte[bodyLen];
					in.readBytes(bbbb);
					System.out.println("JladderCryptoInHandler.java ------> " + in + ", refCnt=" + in.refCnt() + ", read body size=" + bodyLen + ", data=" + Arrays.toString(bbbb));
				}
				return isCompldted;
			} else {
				// 
				return true;
			}
		} finally {
			in.resetReaderIndex();
		}
	}
}
