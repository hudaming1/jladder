package org.jladder.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdCenter {

	public static final String Machine = System.nanoTime() + "";
	private static final Map<String, AtomicInteger> CENTER = new ConcurrentHashMap<>();
	
	public static String gen(String prefix) {
		if (!CENTER.containsKey(prefix)) {
			CENTER.putIfAbsent(prefix, new AtomicInteger(0));
		}
		
		AtomicInteger counter = CENTER.get(prefix);
		
		return Machine + "-" + prefix + "-" + counter.getAndIncrement();
	}
}
