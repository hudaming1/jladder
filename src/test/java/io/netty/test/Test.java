package io.netty.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					System.out.println("start1");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		Future<?> submit = Executors.newFixedThreadPool(1).submit(t1);
		submit.get();
		
		System.out.println("start2");
	}
}
