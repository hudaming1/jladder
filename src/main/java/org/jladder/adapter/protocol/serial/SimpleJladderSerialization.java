package org.jladder.adapter.protocol.serial;

import org.jladder.adapter.protocol.crypto.CryptoFactory;
import org.jladder.adapter.protocol.enumtype.JladderMessageTypeEnum;
import org.jladder.adapter.protocol.message.JladderDataMessage;
import org.jladder.adapter.protocol.message.JladderDisconnectMessage;
import org.jladder.adapter.protocol.message.JladderMessage;
import org.jladder.adapter.protocol.message.JladderMessageBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleJladderSerialization implements JladderSerialization {

	protected final long MAGIC_NUMBER = 0x90ABCDEF;
	
	@Override
	public ByteBuf serial(JladderMessage message) {
		if (message instanceof JladderDataMessage) {
			return toJladdeDataMessage(message);
		} else if (message instanceof JladderDisconnectMessage) {
			return toJladderDisconnectMessage(message);
		}
		return null;
	}

	private ByteBuf toJladderDisconnectMessage(JladderMessage message) {
		JladderDisconnectMessage disconnectMessage = ((JladderDisconnectMessage) message);
		ByteBuf buf = Unpooled.buffer();
		buf.writeLong(MAGIC_NUMBER);
		buf.writeLong(message.getMsgId());
		buf.writeShort(disconnectMessage.getMessageType());
		buf.writeInt(disconnectMessage.getClientIden().length());
		buf.writeBytes(disconnectMessage.getClientIden().getBytes());
		return buf;
	}

	private ByteBuf toJladdeDataMessage(JladderMessage message) {
		JladderDataMessage dataMsg = ((JladderDataMessage) message);
		byte[] hostBytes4Encrypt = CryptoFactory.get().encrypt(dataMsg.getHost().getBytes());
		ByteBuf body = dataMsg.getBody();
		
		byte[] bodyArr = new byte[body.readableBytes()];
		body.readBytes(bodyArr);
		body.release();
		// TODO 如果不需要加密，则直接用CompositeByteBuf组合即可
		byte[] bodyBytes4Encrypt = dataMsg.isBodyNeedEncrypt() ? CryptoFactory.get().encrypt(bodyArr) : bodyArr;
		
		ByteBuf buf = Unpooled.buffer();
		buf.writeLong(MAGIC_NUMBER);
		buf.writeLong(message.getMsgId());
		buf.writeShort(dataMsg.getMessageType());
		buf.writeInt(dataMsg.getClientIden().length());
		buf.writeBytes(dataMsg.getClientIden().getBytes());
		buf.writeInt(hostBytes4Encrypt.length);
		buf.writeBytes(hostBytes4Encrypt);
		buf.writeInt(dataMsg.getPort());
		buf.writeBoolean(dataMsg.isBodyNeedEncrypt());
		buf.writeInt(bodyBytes4Encrypt.length);
		buf.writeBytes(bodyBytes4Encrypt);
		return buf;
	}

	@Override
	public JladderMessage deserial(ByteBuf in) {
		in.skipBytes(8); // skip magic_number
		long msgId = in.readLong();
		short msgType = in.readShort();
		JladderMessageTypeEnum messageType = JladderMessageTypeEnum.getEnum(msgType);
		if (messageType == JladderMessageTypeEnum.Data) {
			// read client_iden
			int idenLen = in.readInt();
			byte[] idenBytes = new byte[idenLen];
			in.readBytes(idenBytes);
			String clientIden = new String(idenBytes);
			// read host
			int hostLen = in.readInt();
			byte[] hostBytes4Encrypt = new byte[hostLen];
			in.readBytes(hostBytes4Encrypt);
			byte[] hostBytes = CryptoFactory.get().decrypt(hostBytes4Encrypt);
			// read port
			int port = in.readInt();
			// read body
			boolean isBodyNeedDecrypt = in.readBoolean();
			int bodyLen = in.readInt();
			byte[] sourceBodyBytes = new byte[bodyLen];
			in.readBytes(sourceBodyBytes);
			byte[] bodyBytes = isBodyNeedDecrypt ? CryptoFactory.get().decrypt(sourceBodyBytes) : sourceBodyBytes;
			ByteBuf body = Unpooled.buffer(bodyLen);
			body.writeBytes(bodyBytes);
			return JladderMessageBuilder.buildNeedEncryptMessage(msgId, clientIden, new String(hostBytes), port, body);
		} else if (messageType == JladderMessageTypeEnum.Disconnect) {
			int idenLen = in.readInt();
			byte[] idenBytes = new byte[idenLen];
			in.readBytes(idenBytes);
			String clientIden = new String(idenBytes);
			return JladderMessageBuilder.buildDisconnectMessage(msgId, clientIden);
		} else {
			log.error("unsupport message-type found: " + msgType);
			return null;
		}
	}

}
