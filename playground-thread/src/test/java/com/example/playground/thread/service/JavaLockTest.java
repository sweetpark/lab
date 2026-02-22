package com.example.playground.thread.service;

import com.example.playground.thread.service.java.MutexService;
import com.example.playground.thread.service.java.SemaphoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class JavaLockTest {
    private MutexService mutexService;
    private SemaphoreService semaphoreService;

    private ExecutorService executorService;
    private CountDownLatch startLatch;
    private CountDownLatch endLatch;


    private static final int POOL_SIZE =
            Runtime.getRuntime().availableProcessors() * 2;

    private static final int THREAD_COUNT = 1000;

    @BeforeEach
    void setUp() {
        mutexService = new MutexService();
        semaphoreService = new SemaphoreService();

        executorService = Executors.newFixedThreadPool(POOL_SIZE);
        startLatch = new CountDownLatch(1);
        endLatch   = new CountDownLatch(THREAD_COUNT);
    }

    @Test
    void mutexTpsTest() throws InterruptedException {

        for(int i = 0; i <THREAD_COUNT; i++ ){
            executorService.execute( () -> {
                try {
                    startLatch.await();
                    mutexService.updateCount();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally{
                    endLatch.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();
        startLatch.countDown(); // 모든 스레드 동시 출발
        endLatch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        double tps = (THREAD_COUNT * 1000.0) / totalTime;

        System.out.println("time : " + totalTime + " ms");
        System.out.println("TPS : " + tps);

        Assertions.assertEquals(THREAD_COUNT, mutexService.getCount());

        executorService.shutdown();
    }

    @Test
    void semaphoreTpsTest() throws InterruptedException {
        for(int i = 0; i <THREAD_COUNT; i++ ){
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    semaphoreService.updateCount();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    endLatch.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();
        startLatch.countDown(); // 모든 스레드 동시 출발
        endLatch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;


        double tps = (THREAD_COUNT * 1000.0) / totalTime;

        System.out.println("time : " + totalTime + " ms");
        System.out.println("TPS : " + tps);

        Assertions.assertEquals(THREAD_COUNT, semaphoreService.getCount());

        executorService.shutdown();
    }
}