package org.jladder.adapter.protocol.executor;

import java.util.List;

import org.jladder.adapter.protocol.JladderMessage;
import org.jladder.common.util.AESCoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class JladderCryptoInHandler extends ReplayingDecoder<JladderMessage> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.skipBytes(8); // skip magic_number
		in.skipBytes(2); // skip type
		// read client_iden
		int idenLen = in.readInt();
		byte[] idenBytes = new byte[idenLen];
		in.readBytes(idenBytes);
		String clientIden = new String(idenBytes);
		// read host
		int hostLen = in.readInt();
		byte[] hostBytes4Encrypt = new byte[hostLen];
		in.readBytes(hostBytes4Encrypt);
		byte[] hostBytes = aesDecrypt(hostBytes4Encrypt);
		// read port
		int port = in.readInt();
		// read body
		boolean isBodyNeedDecrypt = in.readBoolean();
		int bodyLen = in.readInt();
		byte[] sourceBodyBytes = new byte[bodyLen];
		in.readBytes(sourceBodyBytes);
		byte[] bodyBytes = isBodyNeedDecrypt ? aesDecrypt(sourceBodyBytes) : sourceBodyBytes;
		ByteBuf body = Unpooled.buffer(bodyLen);
		body.writeBytes(bodyBytes);
		out.add(JladderMessage.buildNeedEncryptMessage(clientIden, new String(hostBytes), port, body));
	}

	private byte[] aesDecrypt(byte[] bytes) {
		return AESCoder.decrypt(bytes);
	}
}
