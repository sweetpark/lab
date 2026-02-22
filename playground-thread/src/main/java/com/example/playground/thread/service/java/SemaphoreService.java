package com.example.playground.thread.service.java;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JAVA ( 하나의 JVM 위에서 작동 )
 * Lock Pool 존재
 *
 * [Trouble Shooting]
 * ✔ InterruptedException은 RuntimeException으로 막 바꾸면 안 됨
 * ✔ 반드시 interrupt 복원 필요
 * ✔ 의미 있는 예외 사용
 * ✔ acquire 성공 후 release
 * ✔ count는 AtomicInteger 권장
 *
 */
@Slf4j
@Service
public class SemaphoreService {

    private final AtomicInteger count = new AtomicInteger(0);
    private final Semaphore semaphore = new Semaphore(5);

    public void updateCount() throws InterruptedException {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // thread interrupt 복원
            throw new IllegalStateException("Semaphore interrupted", e);
        }

        try {
            count.incrementAndGet();
            Thread.sleep(1);
        }finally{
            semaphore.release();
        }
    }

    public int getCount(){
        return count.get();
    }
}
