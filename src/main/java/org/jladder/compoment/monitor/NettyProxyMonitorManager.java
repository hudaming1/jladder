package org.jladder.compoment.monitor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NettyProxyMonitorManager {

	// 当前连接数
	private static final AtomicInteger ConnectionCounter = new AtomicInteger();
	// in-bytes-length
	private static final AtomicLong InBytesLengthCounter = new AtomicLong();
	// out-bytes-length
	private static final AtomicLong OutBytesLengthCounter = new AtomicLong();
	
	public int increaseConnCount() {
		return ConnectionCounter.incrementAndGet();
	}
	
	public int decreaseConnCount() {
		return ConnectionCounter.decrementAndGet();
	}
	
	public int getConnectionCount() {
		return ConnectionCounter.get();
	}
	
	public long increaseInBytesLength(int length) {
		return InBytesLengthCounter.addAndGet(length);
	}
	
	public long increaseOutBytesLength(int length) {
		return OutBytesLengthCounter.addAndGet(length);
	}
	
	public long getInBytesLength() {
		return InBytesLengthCounter.get();
	}
	
	public long getOutBytesLength() {
		return OutBytesLengthCounter.get();
	}
}
