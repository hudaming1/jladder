package org.hum.tls;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadTest {
	
	private static final Config config = new Config("huming");

	private static ThreadFactory threadFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return new MyThread(r, config);
		}
	};
	
	public static void main(String[] args) {

		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(0, 10, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), threadFactory);
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println(this);
				System.out.println(Thread.currentThread());
				System.out.println("hahaha");
			}
		});
	}
}

class MyThread extends Thread {
	
	private Config config;
	
	public MyThread(Runnable r, Config config) {
		super(r);
		this.config = config;
	}
	
	public Config getConfig() {
		return config;
	}
}

class Config {
	public String key = "";
	
	public Config(String key) {
		this.key = key;
	}
}
