package org.jladder.core.executor;

import java.util.List;

import org.jladder.core.enumtype.JladderMessageTypeEnum;
import org.jladder.core.message.JladderDataMessage;
import org.jladder.core.message.JladderMessage;
import org.jladder.core.serial.JladderSerialization;
import org.jladder.core.serial.SimpleJladderSerialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

//public class JladderCryptoInHandler extends ReplayingDecoder<JladderMessage> {
//
//	private final JladderSerialization jladderSerialization = new SimpleJladderSerialization();
//
//	@Override
//	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//		out.add(jladderSerialization.deserial(in));
//	}
//}

@Slf4j
public class JladderCryptoInHandler extends ByteToMessageDecoder {

	private final JladderSerialization jladderSerialization = new SimpleJladderSerialization();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {

		if (buf.readableBytes() < 18) {
			return;
		}
		
		if (!isCompleted(buf)) {
			return ;
		}
		
		long r = System.nanoTime();
		
		// ⑤ outside接收到inside数据，但未解析，消息目前仍是ByteBuf类型
		// ⑪ inside收到outside数据，但未解析，消息目前仍是ByteBuf类型
		log.info("[" + r + "]接收inside/outside传来数据，长度=" + buf.readableBytes());
		
		JladderMessage jladderMsg = jladderSerialization.deserial(buf);
		
		int len = 0;
		if (jladderMsg instanceof JladderDataMessage) {
			len = ((JladderDataMessage) jladderMsg).getBody().readableBytes();
		}
		// ⑥ outside接收到inside数据，解析完成，消息已经被解码成JladderMessage类型
		// ⑫ inside接收到outside数据，解析完成，消息已经被解码成JladderMessage类型
		log.info("[" + r + "][" + jladderMsg.getClientIden() + "]解码完成，消息Id=" + jladderMsg.getMsgId() + ", 传输数据长度=" + len);
		
		out.add(jladderMsg);
	}
	
	private boolean isCompleted(ByteBuf in) {
		in.markReaderIndex();
		try {
			int readableBytes = in.readableBytes();
			in.skipBytes(16); // skip magic_number
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
				// 31=(magicNumber+messageType+messageId....)
				return readableBytes >= bodyLen + 31 + idenLen + hostLen;
			} else {
				// 
				return true;
			}
		} finally {
			in.resetReaderIndex();
		}
	}
}
