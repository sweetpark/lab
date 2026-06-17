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
public class VirtualThreadService {

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    // 가상 스레드 I/O 대기 시뮬레이션.
    //
    // Thread.sleep(100ms) 블로킹 시 JVM이 자동으로 캐리어 스레드(OS 스레드)를 회수해
    // 다른 가상 스레드가 해당 캐리어 스레드에서 실행된다.
    //
    // 100명이 동시 요청해도 OS 스레드가 100개 필요하지 않다.
    // 적은 수의 캐리어 스레드로 수천 개의 가상 스레드를 처리할 수 있어 TPS가 높다.
    public CompletableFuture<String> ioTask() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String threadName = Thread.currentThread().toString();
                // isVirtual(): Java 21에서 추가된 메서드. 가상 스레드인지 확인한다.
                boolean isVirtual = Thread.currentThread().isVirtual();
                log.info("[VirtualThread] ioTask isVirtual={}, threadName={}",
                        isVirtual, Thread.currentThread().getName());
                Thread.sleep(100);
                return "virtual thread (isVirtual=" + isVirtual + "): " + threadName;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }, virtualThreadExecutor);
    }

    // CPU 집약 연산: 플랫폼 스레드와 동일한 로직.
    //
    // CPU 연산 중에는 가상 스레드도 캐리어 스레드를 점유(핀닝)한다.
    // I/O가 없으므로 캐리어 스레드를 반납할 기회가 없어 성능 이점이 사라진다.
    // 이 테스트는 "가상 스레드는 I/O 바운드 작업에 유리하다"는 원칙을 직접 검증한다.
    public CompletableFuture<String> cpuTask() {
        return CompletableFuture.supplyAsync(() -> {
            long count = 0;

            for (int i = 2; i < 50000; i++) {
                if (isPrime(i)) {
                    count++;
                }
            }

            return "virtual thread. 소수 개수=" + count;
        }, virtualThreadExecutor);
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
