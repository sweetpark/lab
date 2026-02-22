package com.example.playground.thread.service.java;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/***
 * [Mutex (Mutual Exclusion)]
 * JAVA의 ReentrantLock을 이용한 상호 배제 구현
 * 단일 JVM 내에서 특정 자원에 대해 하나의 스레드만 접근하도록 보장합니다.
 *
 * ✔ 장점: 단순하며 임계 영역(Critical Section)을 확실하게 보호함
 * ✔ 단점: 락 획득을 위해 스레드가 대기(Blocking)하므로 처리량이 저하될 수 있음
 */
@Slf4j
@Service
public class MutexService {

    private final AtomicInteger count = new AtomicInteger(0);
    // ReentrantLock: 재진입이 가능한 뮤텍스 락
    private final ReentrantLock lock = new ReentrantLock();

    public void updateCount() throws InterruptedException {
        // 락 획득 시도
        lock.lock();
        try {
            /**
             * [주의사항]
             * 반드시 try-finally 블록을 사용하여 unlock()을 호출해야 합니다.
             * 그렇지 않으면 로직 도중 예외 발생 시 락이 해제되지 않아 데드락(Deadlock)이 발생합니다.
             */
            count.incrementAndGet();
            Thread.sleep(1);
        } finally {
            // 반드시 락 해제
            lock.unlock();
        }
    }

    public int getCount(){
        return this.count.get();
    }

}
