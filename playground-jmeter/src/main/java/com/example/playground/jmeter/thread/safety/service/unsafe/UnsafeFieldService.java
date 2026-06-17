package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnsafeFieldService {

    // [문제 케이스] static 공유 변수에 대한 Race Condition.
    //
    // counter++ 는 원자적 연산이 아니다. 내부적으로 3단계로 분리된다:
    //   1) 메모리에서 counter 값을 레지스터로 읽는다 (READ)
    //   2) 레지스터 값을 1 증가시킨다 (INCREMENT)
    //   3) 레지스터 값을 다시 메모리에 쓴다 (WRITE)
    //
    // 스레드 A가 1단계(READ)를 마치고 3단계(WRITE) 전에 스레드 B가 끼어들면,
    // 두 스레드 모두 같은 값을 읽어 각각 +1 해서 쓰게 된다 → 증가분 유실.
    //
    // JMeter로 30명이 각 10회 요청(총 300회)하면 counter가 300보다 작게 나온다.
    private static int counter = 0;

    public void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public void reset() {
        counter = 0;
    }
}
