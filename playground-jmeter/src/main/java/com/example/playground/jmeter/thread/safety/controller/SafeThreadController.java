package com.example.playground.jmeter.thread.safety.controller;

import com.example.playground.jmeter.thread.safety.service.safe.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thread/safe")
@RequiredArgsConstructor
public class SafeThreadController {

    private final SafeAtomicService safeAtomicService;
    private final SafeSynchronizedService safeSynchronizedService;
    private final SafeReentrantLockService safeReentrantLockService;
    private final SafeConcurrentMapService safeConcurrentMapService;
    private final SafeStatelessService safeStatelessService;

    @PostMapping("/atomic")
    public ResponseEntity<String> atomic() {
        int result = safeAtomicService.increment();
        return ResponseEntity.ok("AtomicInteger 증가. 현재=" + result);
    }

    @PostMapping("/synchronized")
    public ResponseEntity<String> synchronizedCounter() {
        safeSynchronizedService.increment();
        return ResponseEntity.ok("synchronized 증가 완료");
    }

    @PostMapping("/reentrant-lock")
    public ResponseEntity<String> reentrantLock() {
        try {
            safeReentrantLockService.increment();
            return ResponseEntity.ok("ReentrantLock 증가 완료");

        } catch (InterruptedException e) {
            // 인터럽트 상태를 복원하고 503으로 응답한다. throws로 전파하면 Spring이 500으로 처리한다.
            Thread.currentThread().interrupt();
            return ResponseEntity.status(503).body("요청 처리 중 인터럽트 발생");
        }
    }

    @PostMapping("/concurrent-map")
    public ResponseEntity<String> concurrentMap(@RequestParam(defaultValue = "default") String key) {
        int count = safeConcurrentMapService.increment(key);
        return ResponseEntity.ok("ConcurrentHashMap 증가. key=" + key + ", count=" + count);
    }

    @PostMapping("/stateless")
    public ResponseEntity<String> stateless(
            @RequestParam(defaultValue = "req") String requestId,
            @RequestParam(defaultValue = "5") int value) {
        return ResponseEntity.ok(safeStatelessService.process(requestId, value));
    }

    // JMeter 테스트 전 초기화
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        try {
            safeAtomicService.reset();
            safeSynchronizedService.reset();
            safeReentrantLockService.reset();
            safeConcurrentMapService.reset();
            return ResponseEntity.ok("초기화 완료");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(503).body("초기화 중 인터럽트 발생");
        }
    }
}
