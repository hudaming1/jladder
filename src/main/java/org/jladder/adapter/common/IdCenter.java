package org.jladder.adapter.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdCenter {

	public static final String IDEN = System.currentTimeMillis() + "";
	private static final Map<String, AtomicInteger> CENTER = new ConcurrentHashMap<>();
	
	public static String gen(String prefix) {
		if (!CENTER.containsKey(prefix)) {
			CENTER.put(prefix, new AtomicInteger(0));
		}
		return IDEN + "-" + prefix + "-" + CENTER.get(prefix).getAndIncrement();
	}
}
