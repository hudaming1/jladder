package org.hum.nettyproxy.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	private String name;
	private AtomicInteger counter = new AtomicInteger(1);

	public NamedThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, name + "-" + counter.getAndIncrement());
	}
}
