package com.example.playground.thread.service;

import com.example.playground.thread.entity.Product;
import com.example.playground.thread.service.db.OptimisticService;
import com.example.playground.thread.service.db.PessimisticService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
class DbLockTest {

    private final int THREAD_SIZE = 1000;
    private final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    @Autowired PessimisticService pessimisticService;
    @Autowired private OptimisticService optimisticService;

    private ExecutorService executorService;
    private CountDownLatch startLatch;
    private CountDownLatch endLatch;

    @Autowired private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM product");
        jdbcTemplate.execute("INSERT INTO product (id, quantity, version) VALUES (1, 1000, 0)");

        executorService = Executors.newFixedThreadPool(POOL_SIZE);
        startLatch = new CountDownLatch(1);
        endLatch = new CountDownLatch(THREAD_SIZE);
    }

    @Test
    @DisplayName("비관적 락")
    void pessimistic() throws InterruptedException {
        for (int i = 0; i < THREAD_SIZE; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    pessimisticService.decreasePessimistic(1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();
        startLatch.countDown();
        endLatch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        double tps = (THREAD_SIZE * 1000.0) / totalTime;

        System.out.println("time : " + totalTime + " ms");
        System.out.println("TPS : " + tps);

        Assertions.assertEquals(0, pessimisticService.getQuantity(1L));
        executorService.shutdown();
    }

    @Test
    @DisplayName("낙관적 락")
    void optimistic() throws InterruptedException {
        for (int i = 0; i < THREAD_SIZE; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    optimisticService.decreaseWithRetry(1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    endLatch.countDown();
                }
            });
        }


        long startTime = System.currentTimeMillis();
        startLatch.countDown();
        endLatch.await();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        double tps = (THREAD_SIZE * 1000.0) / totalTime;

        System.out.println("time : " + totalTime + " ms");
        System.out.println("TPS : " + tps);

        Product resultProduct = optimisticService.getProduct(1L);
        System.out.printf("실패 개수 : %d (정상 성공 개수 : %d)\n" ,
                resultProduct.getQuantity(),
                resultProduct.getVersion());

        Assertions.assertEquals(THREAD_SIZE, resultProduct.getQuantity() + resultProduct.getVersion());

        executorService.shutdown();
    }
}