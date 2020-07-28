package io.netty.test;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufTest {

	public static void main(String[] args) {
		ByteBuf byteBuf = Unpooled.buffer(16);
		
		System.out.println(byteBuf.getClass());
		
		byteBuf.writeInt(1);
		byteBuf.writeInt(2);
		byteBuf.writeInt(3);
		
		byteBuf.resetWriterIndex();
		
		byteBuf.writeInt(4);
		byteBuf.writeInt(5);
		byteBuf.writeInt(6);
		
		System.out.println(byteBuf.capacity());
		ByteBuf duplicate = byteBuf.duplicate();
		duplicate.writeInt(7);
		ByteBuf copy = byteBuf.copy();
		copy.writeInt(11);
		
		System.out.println(Arrays.toString(byteBuf.array()));
		
		byteBuf.retain();
		byteBuf.release();
		
		
		System.out.println(byteBuf.readInt());
		System.out.println(byteBuf.readInt());
		System.out.println(byteBuf.readInt());
		
		byteBuf.resetReaderIndex();
		
		System.out.println(byteBuf.readInt());
		System.out.println(byteBuf.readInt());
		System.out.println(byteBuf.readInt());
		
		System.out.println("duplicate======");

		System.out.println(duplicate.readInt());
		System.out.println(duplicate.readInt());
		System.out.println(duplicate.readInt());
		System.out.println(duplicate.readInt());
		
		System.out.println("copy======");

		System.out.println(copy.readInt());
		System.out.println(copy.readInt());
		System.out.println(copy.readInt());
		System.out.println(copy.readInt());

		
		System.out.println(byteBuf.capacity());
		ByteBuf duplicate2 = byteBuf.duplicate();
		ByteBuf copy2 = byteBuf.copy();
	}
}
