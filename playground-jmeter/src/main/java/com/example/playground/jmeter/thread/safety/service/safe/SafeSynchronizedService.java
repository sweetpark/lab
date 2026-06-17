package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SafeSynchronizedService {

    // [해결책 2] synchronized 블록.
    //
    // 한 번에 하나의 스레드만 이 블록에 진입할 수 있다(상호 배제).
    // 진입하지 못한 스레드는 모니터 락이 해제될 때까지 BLOCKED 상태로 대기한다.
    //
    // [장점] 문법이 직관적이고 Java 기본 제공.
    // [단점] 블로킹 방식이라 대기 스레드가 CPU를 낭비. 타임아웃 설정 불가.
    // 단일 JVM 환경에서만 유효하다(분산 환경에서는 효과 없음).
    private int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public synchronized int getCounter() {
        return counter;
    }

    public synchronized void reset() {
        counter = 0;
    }
}
