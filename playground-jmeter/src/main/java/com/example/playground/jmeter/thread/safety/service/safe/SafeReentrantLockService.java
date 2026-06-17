package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SafeReentrantLockService {

    // [해결책 3] ReentrantLock — synchronized의 고급 버전.
    //
    // synchronized와 동일한 상호 배제를 제공하지만 추가 기능이 있다:
    //   - tryLock(timeout): 지정 시간 내 락을 못 얻으면 포기한다(데드락 방지)
    //   - lockInterruptibly(): 대기 중 인터럽트 허용
    //   - fair=true: 공정 모드 - 먼저 기다린 스레드가 먼저 락 획득(기아 현상 방지)
    //
    // 이 예시에서는 tryLock으로 최대 3초를 기다리고, 실패하면 예외를 던진다.
    private final ReentrantLock lock = new ReentrantLock();

    private int counter = 0;

    public void increment() throws InterruptedException {
        boolean acquired = lock.tryLock(3, TimeUnit.SECONDS);

        if (!acquired) {
            throw new IllegalStateException("락 획득 시간 초과 (3초)");
        }

        try {
            counter++;

        } finally {
            // 반드시 finally에서 해제. 예외 발생 시에도 락이 풀려야 다른 스레드가 진입할 수 있다.
            lock.unlock();
        }
    }

    public int getCounter() {
        lock.lock();
        try {
            return counter;
        } finally {
            lock.unlock();
        }
    }

    public void reset() throws InterruptedException {
        boolean acquired = lock.tryLock(3, TimeUnit.SECONDS);

        if (!acquired) {
            throw new IllegalStateException("락 획득 시간 초과 (3초)");
        }

        try {
            counter = 0;

        } finally {
            lock.unlock();
        }
    }
}
