package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class SafeConcurrentMapService {

    // [해결책 4] ConcurrentHashMap — 스레드 안전한 Map.
    //
    // HashMap은 멀티스레드 환경에서 무한 루프(CPU 100%) 또는 데이터 유실을 일으킨다.
    // ConcurrentHashMap은 버킷 단위 세그먼트 락을 사용해 동시 읽기/쓰기를 허용한다.
    //
    // compute()는 키에 대한 읽기-계산-쓰기를 원자적으로 처리한다.
    // 직접 get() → 계산 → put() 3단계로 나눠 쓰면 Race Condition이 재발한다.
    private final ConcurrentHashMap<String, AtomicInteger> countMap = new ConcurrentHashMap<>();

    public int increment(String key) {
        // computeIfAbsent: 키가 없으면 새 AtomicInteger(0)을 원자적으로 삽입
        return countMap.computeIfAbsent(key, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int getCount(String key) {
        AtomicInteger value = countMap.get(key);
        return value == null ? 0 : value.get();
    }

    public void reset() {
        countMap.clear();
    }
}
