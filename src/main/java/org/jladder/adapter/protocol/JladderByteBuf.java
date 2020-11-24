package org.jladder.adapter.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;

public class JladderByteBuf extends ByteBuf {

	private ByteBuf byteBuf;
	
	public JladderByteBuf(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public int refCnt() {
		return byteBuf.refCnt();
	}

	public boolean release() {
		return byteBuf.release();
	}

	public boolean release(int decrement) {
		return byteBuf.release(decrement);
	}

	public int capacity() {
		return byteBuf.capacity();
	}

	public ByteBuf capacity(int newCapacity) {
		return byteBuf.capacity(newCapacity);
	}

	public int maxCapacity() {
		return byteBuf.maxCapacity();
	}

	public ByteBufAllocator alloc() {
		return byteBuf.alloc();
	}

	@Deprecated
	public ByteOrder order() {
		return byteBuf.order();
	}

	@Deprecated
	public ByteBuf order(ByteOrder endianness) {
		return byteBuf.order(endianness);
	}

	public ByteBuf unwrap() {
		return byteBuf.unwrap();
	}

	public boolean isDirect() {
		return byteBuf.isDirect();
	}

	public boolean isReadOnly() {
		return byteBuf.isReadOnly();
	}

	public ByteBuf asReadOnly() {
		return byteBuf.asReadOnly();
	}

	public int readerIndex() {
		return byteBuf.readerIndex();
	}

	public ByteBuf readerIndex(int readerIndex) {
		return byteBuf.readerIndex(readerIndex);
	}

	public int writerIndex() {
		return byteBuf.writerIndex();
	}

	public ByteBuf writerIndex(int writerIndex) {
		return byteBuf.writerIndex(writerIndex);
	}

	public ByteBuf setIndex(int readerIndex, int writerIndex) {
		return byteBuf.setIndex(readerIndex, writerIndex);
	}

	public int readableBytes() {
		return byteBuf.readableBytes();
	}

	public int writableBytes() {
		return byteBuf.writableBytes();
	}

	public int maxWritableBytes() {
		return byteBuf.maxWritableBytes();
	}

	public boolean isReadable() {
		return byteBuf.isReadable();
	}

	public boolean isReadable(int size) {
		return byteBuf.isReadable(size);
	}

	public boolean isWritable() {
		return byteBuf.isWritable();
	}

	public boolean isWritable(int size) {
		return byteBuf.isWritable(size);
	}

	public ByteBuf clear() {
		return byteBuf.clear();
	}

	public ByteBuf markReaderIndex() {
		return byteBuf.markReaderIndex();
	}

	public ByteBuf resetReaderIndex() {
		return byteBuf.resetReaderIndex();
	}

	public ByteBuf markWriterIndex() {
		return byteBuf.markWriterIndex();
	}

	public ByteBuf resetWriterIndex() {
		return byteBuf.resetWriterIndex();
	}

	public ByteBuf discardReadBytes() {
		return byteBuf.discardReadBytes();
	}

	public ByteBuf discardSomeReadBytes() {
		return byteBuf.discardSomeReadBytes();
	}

	public ByteBuf ensureWritable(int minWritableBytes) {
		return byteBuf.ensureWritable(minWritableBytes);
	}

	public int ensureWritable(int minWritableBytes, boolean force) {
		return byteBuf.ensureWritable(minWritableBytes, force);
	}

	public boolean getBoolean(int index) {
		return byteBuf.getBoolean(index);
	}

	public byte getByte(int index) {
		return byteBuf.getByte(index);
	}

	public short getUnsignedByte(int index) {
		return byteBuf.getUnsignedByte(index);
	}

	public short getShort(int index) {
		return byteBuf.getShort(index);
	}

	public short getShortLE(int index) {
		return byteBuf.getShortLE(index);
	}

	public int getUnsignedShort(int index) {
		return byteBuf.getUnsignedShort(index);
	}

	public int getUnsignedShortLE(int index) {
		return byteBuf.getUnsignedShortLE(index);
	}

	public int getMedium(int index) {
		return byteBuf.getMedium(index);
	}

	public int getMediumLE(int index) {
		return byteBuf.getMediumLE(index);
	}

	public int getUnsignedMedium(int index) {
		return byteBuf.getUnsignedMedium(index);
	}

	public int getUnsignedMediumLE(int index) {
		return byteBuf.getUnsignedMediumLE(index);
	}

	public int getInt(int index) {
		return byteBuf.getInt(index);
	}

	public int getIntLE(int index) {
		return byteBuf.getIntLE(index);
	}

	public long getUnsignedInt(int index) {
		return byteBuf.getUnsignedInt(index);
	}

	public long getUnsignedIntLE(int index) {
		return byteBuf.getUnsignedIntLE(index);
	}

	public long getLong(int index) {
		return byteBuf.getLong(index);
	}

	public long getLongLE(int index) {
		return byteBuf.getLongLE(index);
	}

	public char getChar(int index) {
		return byteBuf.getChar(index);
	}

	public float getFloat(int index) {
		return byteBuf.getFloat(index);
	}

	public float getFloatLE(int index) {
		return byteBuf.getFloatLE(index);
	}

	public double getDouble(int index) {
		return byteBuf.getDouble(index);
	}

	public double getDoubleLE(int index) {
		return byteBuf.getDoubleLE(index);
	}

	public ByteBuf getBytes(int index, ByteBuf dst) {
		return byteBuf.getBytes(index, dst);
	}

	public ByteBuf getBytes(int index, ByteBuf dst, int length) {
		return byteBuf.getBytes(index, dst, length);
	}

	public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
		return byteBuf.getBytes(index, dst, dstIndex, length);
	}

	public ByteBuf getBytes(int index, byte[] dst) {
		return byteBuf.getBytes(index, dst);
	}

	public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
		return byteBuf.getBytes(index, dst, dstIndex, length);
	}

	public ByteBuf getBytes(int index, ByteBuffer dst) {
		return byteBuf.getBytes(index, dst);
	}

	public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
		return byteBuf.getBytes(index, out, length);
	}

	public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
		return byteBuf.getBytes(index, out, length);
	}

	public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
		return byteBuf.getBytes(index, out, position, length);
	}

	public CharSequence getCharSequence(int index, int length, Charset charset) {
		return byteBuf.getCharSequence(index, length, charset);
	}

	public ByteBuf setBoolean(int index, boolean value) {
		return byteBuf.setBoolean(index, value);
	}

	public ByteBuf setByte(int index, int value) {
		return byteBuf.setByte(index, value);
	}

	public ByteBuf setShort(int index, int value) {
		return byteBuf.setShort(index, value);
	}

	public ByteBuf setShortLE(int index, int value) {
		return byteBuf.setShortLE(index, value);
	}

	public ByteBuf setMedium(int index, int value) {
		return byteBuf.setMedium(index, value);
	}

	public ByteBuf setMediumLE(int index, int value) {
		return byteBuf.setMediumLE(index, value);
	}

	public ByteBuf setInt(int index, int value) {
		return byteBuf.setInt(index, value);
	}

	public ByteBuf setIntLE(int index, int value) {
		return byteBuf.setIntLE(index, value);
	}

	public ByteBuf setLong(int index, long value) {
		return byteBuf.setLong(index, value);
	}

	public ByteBuf setLongLE(int index, long value) {
		return byteBuf.setLongLE(index, value);
	}

	public ByteBuf setChar(int index, int value) {
		return byteBuf.setChar(index, value);
	}

	public ByteBuf setFloat(int index, float value) {
		return byteBuf.setFloat(index, value);
	}

	public ByteBuf setFloatLE(int index, float value) {
		return byteBuf.setFloatLE(index, value);
	}

	public ByteBuf setDouble(int index, double value) {
		return byteBuf.setDouble(index, value);
	}

	public ByteBuf setDoubleLE(int index, double value) {
		return byteBuf.setDoubleLE(index, value);
	}

	public ByteBuf setBytes(int index, ByteBuf src) {
		return byteBuf.setBytes(index, src);
	}

	public ByteBuf setBytes(int index, ByteBuf src, int length) {
		return byteBuf.setBytes(index, src, length);
	}

	public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
		return byteBuf.setBytes(index, src, srcIndex, length);
	}

	public ByteBuf setBytes(int index, byte[] src) {
		return byteBuf.setBytes(index, src);
	}

	public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
		return byteBuf.setBytes(index, src, srcIndex, length);
	}

	public ByteBuf setBytes(int index, ByteBuffer src) {
		return byteBuf.setBytes(index, src);
	}

	public int setBytes(int index, InputStream in, int length) throws IOException {
		return byteBuf.setBytes(index, in, length);
	}

	public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
		return byteBuf.setBytes(index, in, length);
	}

	public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
		return byteBuf.setBytes(index, in, position, length);
	}

	public ByteBuf setZero(int index, int length) {
		return byteBuf.setZero(index, length);
	}

	public int setCharSequence(int index, CharSequence sequence, Charset charset) {
		return byteBuf.setCharSequence(index, sequence, charset);
	}

	public boolean readBoolean() {
		return byteBuf.readBoolean();
	}

	public byte readByte() {
		return byteBuf.readByte();
	}

	public short readUnsignedByte() {
		return byteBuf.readUnsignedByte();
	}

	public short readShort() {
		return byteBuf.readShort();
	}

	public short readShortLE() {
		return byteBuf.readShortLE();
	}

	public int readUnsignedShort() {
		return byteBuf.readUnsignedShort();
	}

	public int readUnsignedShortLE() {
		return byteBuf.readUnsignedShortLE();
	}

	public int readMedium() {
		return byteBuf.readMedium();
	}

	public int readMediumLE() {
		return byteBuf.readMediumLE();
	}

	public int readUnsignedMedium() {
		return byteBuf.readUnsignedMedium();
	}

	public int readUnsignedMediumLE() {
		return byteBuf.readUnsignedMediumLE();
	}

	public int readInt() {
		return byteBuf.readInt();
	}

	public int readIntLE() {
		return byteBuf.readIntLE();
	}

	public long readUnsignedInt() {
		return byteBuf.readUnsignedInt();
	}

	public long readUnsignedIntLE() {
		return byteBuf.readUnsignedIntLE();
	}

	public long readLong() {
		return byteBuf.readLong();
	}

	public long readLongLE() {
		return byteBuf.readLongLE();
	}

	public char readChar() {
		return byteBuf.readChar();
	}

	public float readFloat() {
		return byteBuf.readFloat();
	}

	public float readFloatLE() {
		return byteBuf.readFloatLE();
	}

	public double readDouble() {
		return byteBuf.readDouble();
	}

	public double readDoubleLE() {
		return byteBuf.readDoubleLE();
	}

	public ByteBuf readBytes(int length) {
		return byteBuf.readBytes(length);
	}

	public ByteBuf readSlice(int length) {
		return byteBuf.readSlice(length);
	}

	public ByteBuf readRetainedSlice(int length) {
		return byteBuf.readRetainedSlice(length);
	}

	public ByteBuf readBytes(ByteBuf dst) {
		return byteBuf.readBytes(dst);
	}

	public ByteBuf readBytes(ByteBuf dst, int length) {
		return byteBuf.readBytes(dst, length);
	}

	public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
		return byteBuf.readBytes(dst, dstIndex, length);
	}

	public ByteBuf readBytes(byte[] dst) {
		return byteBuf.readBytes(dst);
	}

	public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
		return byteBuf.readBytes(dst, dstIndex, length);
	}

	public ByteBuf readBytes(ByteBuffer dst) {
		return byteBuf.readBytes(dst);
	}

	public ByteBuf readBytes(OutputStream out, int length) throws IOException {
		return byteBuf.readBytes(out, length);
	}

	public int readBytes(GatheringByteChannel out, int length) throws IOException {
		return byteBuf.readBytes(out, length);
	}

	public CharSequence readCharSequence(int length, Charset charset) {
		return byteBuf.readCharSequence(length, charset);
	}

	public int readBytes(FileChannel out, long position, int length) throws IOException {
		return byteBuf.readBytes(out, position, length);
	}

	public ByteBuf skipBytes(int length) {
		return byteBuf.skipBytes(length);
	}

	public ByteBuf writeBoolean(boolean value) {
		return byteBuf.writeBoolean(value);
	}

	public ByteBuf writeByte(int value) {
		return byteBuf.writeByte(value);
	}

	public ByteBuf writeShort(int value) {
		return byteBuf.writeShort(value);
	}

	public ByteBuf writeShortLE(int value) {
		return byteBuf.writeShortLE(value);
	}

	public ByteBuf writeMedium(int value) {
		return byteBuf.writeMedium(value);
	}

	public ByteBuf writeMediumLE(int value) {
		return byteBuf.writeMediumLE(value);
	}

	public ByteBuf writeInt(int value) {
		return byteBuf.writeInt(value);
	}

	public ByteBuf writeIntLE(int value) {
		return byteBuf.writeIntLE(value);
	}

	public ByteBuf writeLong(long value) {
		return byteBuf.writeLong(value);
	}

	public ByteBuf writeLongLE(long value) {
		return byteBuf.writeLongLE(value);
	}

	public ByteBuf writeChar(int value) {
		return byteBuf.writeChar(value);
	}

	public ByteBuf writeFloat(float value) {
		return byteBuf.writeFloat(value);
	}

	public ByteBuf writeFloatLE(float value) {
		return byteBuf.writeFloatLE(value);
	}

	public ByteBuf writeDouble(double value) {
		return byteBuf.writeDouble(value);
	}

	public ByteBuf writeDoubleLE(double value) {
		return byteBuf.writeDoubleLE(value);
	}

	public ByteBuf writeBytes(ByteBuf src) {
		return byteBuf.writeBytes(src);
	}

	public ByteBuf writeBytes(ByteBuf src, int length) {
		return byteBuf.writeBytes(src, length);
	}

	public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
		return byteBuf.writeBytes(src, srcIndex, length);
	}

	public ByteBuf writeBytes(byte[] src) {
		return byteBuf.writeBytes(src);
	}

	public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
		return byteBuf.writeBytes(src, srcIndex, length);
	}

	public ByteBuf writeBytes(ByteBuffer src) {
		return byteBuf.writeBytes(src);
	}

	public int writeBytes(InputStream in, int length) throws IOException {
		return byteBuf.writeBytes(in, length);
	}

	public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
		return byteBuf.writeBytes(in, length);
	}

	public int writeBytes(FileChannel in, long position, int length) throws IOException {
		return byteBuf.writeBytes(in, position, length);
	}

	public ByteBuf writeZero(int length) {
		return byteBuf.writeZero(length);
	}

	public int writeCharSequence(CharSequence sequence, Charset charset) {
		return byteBuf.writeCharSequence(sequence, charset);
	}

	public int indexOf(int fromIndex, int toIndex, byte value) {
		return byteBuf.indexOf(fromIndex, toIndex, value);
	}

	public int bytesBefore(byte value) {
		return byteBuf.bytesBefore(value);
	}

	public int bytesBefore(int length, byte value) {
		return byteBuf.bytesBefore(length, value);
	}

	public int bytesBefore(int index, int length, byte value) {
		return byteBuf.bytesBefore(index, length, value);
	}

	public int forEachByte(ByteProcessor processor) {
		return byteBuf.forEachByte(processor);
	}

	public int forEachByte(int index, int length, ByteProcessor processor) {
		return byteBuf.forEachByte(index, length, processor);
	}

	public int forEachByteDesc(ByteProcessor processor) {
		return byteBuf.forEachByteDesc(processor);
	}

	public int forEachByteDesc(int index, int length, ByteProcessor processor) {
		return byteBuf.forEachByteDesc(index, length, processor);
	}

	public ByteBuf copy() {
		return byteBuf.copy();
	}

	public ByteBuf copy(int index, int length) {
		return byteBuf.copy(index, length);
	}

	public ByteBuf slice() {
		return byteBuf.slice();
	}

	public ByteBuf retainedSlice() {
		return byteBuf.retainedSlice();
	}

	public ByteBuf slice(int index, int length) {
		return byteBuf.slice(index, length);
	}

	public ByteBuf retainedSlice(int index, int length) {
		return byteBuf.retainedSlice(index, length);
	}

	public ByteBuf duplicate() {
		return byteBuf.duplicate();
	}

	public ByteBuf retainedDuplicate() {
		return byteBuf.retainedDuplicate();
	}

	public int nioBufferCount() {
		return byteBuf.nioBufferCount();
	}

	public ByteBuffer nioBuffer() {
		return byteBuf.nioBuffer();
	}

	public ByteBuffer nioBuffer(int index, int length) {
		return byteBuf.nioBuffer(index, length);
	}

	public ByteBuffer internalNioBuffer(int index, int length) {
		return byteBuf.internalNioBuffer(index, length);
	}

	public ByteBuffer[] nioBuffers() {
		return byteBuf.nioBuffers();
	}

	public ByteBuffer[] nioBuffers(int index, int length) {
		return byteBuf.nioBuffers(index, length);
	}

	public boolean hasArray() {
		return byteBuf.hasArray();
	}

	public byte[] array() {
		return byteBuf.array();
	}

	public int arrayOffset() {
		return byteBuf.arrayOffset();
	}

	public boolean hasMemoryAddress() {
		return byteBuf.hasMemoryAddress();
	}

	public long memoryAddress() {
		return byteBuf.memoryAddress();
	}

	public String toString(Charset charset) {
		return byteBuf.toString(charset);
	}

	public String toString(int index, int length, Charset charset) {
		return byteBuf.toString(index, length, charset);
	}

	public int hashCode() {
		return byteBuf.hashCode();
	}

	public boolean equals(Object obj) {
		return byteBuf.equals(obj);
	}

	public int compareTo(ByteBuf buffer) {
		return byteBuf.compareTo(buffer);
	}

	public String toString() {
		return byteBuf.toString();
	}

	public ByteBuf retain(int increment) {
		return byteBuf.retain(increment);
	}

	public ByteBuf retain() {
		return byteBuf.retain();
	}

	public ByteBuf touch() {
		return byteBuf.touch();
	}

	public ByteBuf touch(Object hint) {
		return byteBuf.touch(hint);
	}
	
	public ByteBuf toByteBuf() {
		return this.byteBuf;
	}
}
