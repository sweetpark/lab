package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class SafeAtomicService {

    // [해결책 1] AtomicInteger — Lock-Free CAS(Compare-And-Swap) 방식.
    //
    // CPU 하드웨어 수준의 원자적 명령(CMPXCHG)을 사용한다.
    // incrementAndGet()은 "읽기-증가-쓰기"를 단일 원자 연산으로 처리하므로 스레드 간 충돌이 없다.
    //
    // [장점] synchronized보다 빠르다. 락이 없어 컨텍스트 스위치가 발생하지 않는다.
    // [단점] 단일 변수에만 적용 가능. 복합 연산(조건+변경)은 별도 처리가 필요하다.
    private final AtomicInteger counter = new AtomicInteger(0);

    public int increment() {
        return counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }
}
