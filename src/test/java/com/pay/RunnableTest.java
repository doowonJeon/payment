package com.pay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.pay.util.AESUtil;

public class RunnableTest {

	private static Integer count = 0;
	private static ReentrantLock rl = new ReentrantLock();

	@Test
	public void Test_1() throws Exception {

		ExecutorService service = Executors.newFixedThreadPool(1000);
//		Runnable runnable = () -> {
//			synchronized (count) {
//				count++;
//			}
//		};

		Runnable runnable = () -> {
			try {
				rl.lock();
				count++;
			} finally {
				rl.unlock();
			}
		};

		for (int i = 0; i < 100000; i++) {
			service.execute(runnable);
		}

		Thread.sleep(2000L);
		service.shutdown();

		System.out.println(count);
	}
}
