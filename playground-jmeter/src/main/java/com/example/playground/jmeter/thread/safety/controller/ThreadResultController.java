package com.example.playground.jmeter.thread.safety.controller;

import com.example.playground.jmeter.thread.safety.service.safe.*;
import com.example.playground.jmeter.thread.safety.service.unsafe.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/thread/result")
@RequiredArgsConstructor
public class ThreadResultController {

    private final UnsafeFieldService unsafeFieldService;
    private final SafeAtomicService safeAtomicService;
    private final SafeSynchronizedService safeSynchronizedService;
    private final SafeReentrantLockService safeReentrantLockService;
    private final UnsafeCheckThenActService unsafeCheckThenActService;

    // JMeter 테스트 완료 후 이 엔드포인트로 최종 카운터 값을 확인한다.
    // expected=300(30명 × 10회)과 actual 값을 비교하면 Race Condition 발생 여부를 즉시 알 수 있다.
    @GetMapping
    public ResponseEntity<Map<String, Object>> result(
            @RequestParam(defaultValue = "300") int expected) {

        int unsafeCount = unsafeFieldService.getCounter();
        int atomicCount = safeAtomicService.getCounter();
        int synchronizedCount = safeSynchronizedService.getCounter();
        int reentrantCount = safeReentrantLockService.getCounter();

        return ResponseEntity.ok(Map.of(
                "expected", expected,
                "unsafe_field", Map.of("actual", unsafeCount, "correct", unsafeCount == expected),
                "safe_atomic", Map.of("actual", atomicCount, "correct", atomicCount == expected),
                "safe_synchronized", Map.of("actual", synchronizedCount, "correct", synchronizedCount == expected),
                "safe_reentrant_lock", Map.of("actual", reentrantCount, "correct", reentrantCount == expected),
                "unsafe_stock", unsafeCheckThenActService.getStock()
        ));
    }
}
