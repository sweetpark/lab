package com.example.playground.jmeter.thread.virtual.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformThreadService {

    // @Qualifier로 플랫폼 스레드 풀을 명시적으로 주입한다.
    @Qualifier("platformThreadExecutor")
    private final ExecutorService platformThreadExecutor;

    // I/O 대기 시뮬레이션: Thread.sleep(100ms)으로 DB 쿼리나 외부 API 호출 대기를 흉내낸다.
    //
    // 플랫폼 스레드 200개 풀에서 실행한다.
    // 100명이 동시에 요청하면 100개 스레드가 100ms 동안 모두 블로킹 상태가 된다.
    // 201번째 요청은 스레드가 남을 때까지 큐에서 대기한다.
    public CompletableFuture<String> ioTask() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String threadName = Thread.currentThread().toString();
                log.info("[PlatformThread] ioTask threadName={}", Thread.currentThread().getName());
                Thread.sleep(100);
                return "platform thread: " + threadName;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }, platformThreadExecutor);
    }

    // CPU 집약 연산: 소수 판별 반복 계산.
    // 플랫폼 스레드와 가상 스레드 모두 OS 코어를 점유해야 하므로 차이가 없다.
    // JMeter에서 두 엔드포인트를 비교해 "가상 스레드 = 항상 빠른 것이 아님"을 확인한다.
    public CompletableFuture<String> cpuTask() {
        return CompletableFuture.supplyAsync(() -> {
            long count = 0;

            for (int i = 2; i < 50000; i++) {
                if (isPrime(i)) {
                    count++;
                }
            }

            return "platform thread. 소수 개수=" + count;
        }, platformThreadExecutor);
    }

    // 학습 목적: 플랫폼/가상 스레드 서비스가 동일 로직을 각자 실행함을 명시적으로 보여주기 위해 중복을 허용한다.
    private boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
}
