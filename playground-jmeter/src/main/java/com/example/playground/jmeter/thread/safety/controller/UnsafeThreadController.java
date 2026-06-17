package com.example.playground.jmeter.thread.safety.controller;

import com.example.playground.jmeter.thread.safety.service.unsafe.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thread/unsafe")
@RequiredArgsConstructor
public class UnsafeThreadController {

    private final UnsafeFieldService unsafeFieldService;
    private final UnsafeListService unsafeListService;
    private final UnsafeCheckThenActService unsafeCheckThenActService;
    private final UnsafeSingletonFieldService unsafeSingletonFieldService;

    @PostMapping("/field")
    public ResponseEntity<String> field() {
        unsafeFieldService.increment();
        return ResponseEntity.ok("카운터 증가 요청 완료");
    }

    @PostMapping("/list")
    public ResponseEntity<String> list(@RequestParam(defaultValue = "item") String value) {
        unsafeListService.add(value);
        return ResponseEntity.ok("리스트 추가 완료. 현재크기=" + unsafeListService.size());
    }

    @PostMapping("/check-then-act")
    public ResponseEntity<String> checkThenAct() {
        return ResponseEntity.ok(unsafeCheckThenActService.decreaseStock());
    }

    @PostMapping("/singleton-field")
    public ResponseEntity<String> singletonField(@RequestParam(defaultValue = "req") String requestId) {
        return ResponseEntity.ok(unsafeSingletonFieldService.process(requestId));
    }

    // JMeter 테스트 전 초기화 엔드포인트
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        unsafeFieldService.reset();
        unsafeListService.reset();
        unsafeCheckThenActService.reset();
        unsafeSingletonFieldService.reset();
        return ResponseEntity.ok("초기화 완료");
    }
}
