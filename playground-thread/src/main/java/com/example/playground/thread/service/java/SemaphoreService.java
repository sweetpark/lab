package com.example.playground.thread.service.java;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [Semaphore]
 * 특정 개수(Permits)의 스레드만 동시에 자원에 접근할 수 있도록 제어합니다.
 *
 * ✔ 장점: 동시에 실행 가능한 작업 수를 제한하여 서버 리소스 보호 (Throttling)
 * ✔ 단점: 상호 배제(Mutex)와 달리 다수의 스레드가 진입하므로 데이터 일관성을 위해 Atomic 등 추가 보호 필요
 */
@Slf4j
@Service
public class SemaphoreService {

    private final AtomicInteger count = new AtomicInteger(0);
    // 5개의 허가(Permit)를 가진 세마포어
    private final Semaphore semaphore = new Semaphore(5);

    public void updateCount() throws InterruptedException {
        /**
         * [Trouble Shooting & 주의사항]
         * ✔ InterruptedException 처리: interrupt 신호 시 복원(Thread.currentThread().interrupt()) 필수
         * ✔ acquire() 성공 후 반드시 release(): 허가권을 반납하지 않으면 다른 스레드가 진입할 수 없음
         * ✔ 의미 있는 예외 처리: 대기 중 인터럽트 발생 시 처리 전략 수립
         */
        try {
            // 허가권 획득 시도 (사용 가능한 허가권이 없으면 Blocking)
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
            throw new IllegalStateException("Semaphore 획득 대기 중 중단됨", e);
        }

        try {
            count.incrementAndGet();
            Thread.sleep(1);
        } finally {
            // 사용 완료 후 반드시 허가권 반납
            semaphore.release();
        }
    }

    public int getCount(){
        return count.get();
    }
}
