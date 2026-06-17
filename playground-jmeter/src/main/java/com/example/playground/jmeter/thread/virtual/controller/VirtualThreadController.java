package com.example.playground.jmeter.thread.virtual.controller;

import com.example.playground.jmeter.thread.virtual.service.PlatformThreadService;
import com.example.playground.jmeter.thread.virtual.service.VirtualThreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

// 학습 포인트: .get()으로 CompletableFuture 결과를 동기 블로킹으로 기다린다.
// JMeter가 HTTP 응답 완료 시점을 기준으로 응답시간을 측정하기 위해 의도적으로 블로킹한다.
// 프로덕션에서는 @Async + DeferredResult 또는 WebFlux로 비동기 처리한다.
@Slf4j
@RestController
@RequiredArgsConstructor
public class VirtualThreadController {

    private final PlatformThreadService platformThreadService;
    private final VirtualThreadService virtualThreadService;

    @GetMapping("/api/thread/platform/io-task")
    public ResponseEntity<String> platformIoTask() {
        try {
            return ResponseEntity.ok(platformThreadService.ioTask().get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Interrupted");
        } catch (ExecutionException e) {
            log.error("[platform/io-task] execution failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getCause().getMessage());
        }
    }

    @GetMapping("/api/thread/virtual/io-task")
    public ResponseEntity<String> virtualIoTask() {
        try {
            return ResponseEntity.ok(virtualThreadService.ioTask().get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Interrupted");
        } catch (ExecutionException e) {
            log.error("[virtual/io-task] execution failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getCause().getMessage());
        }
    }

    @GetMapping("/api/thread/platform/cpu-task")
    public ResponseEntity<String> platformCpuTask() {
        try {
            return ResponseEntity.ok(platformThreadService.cpuTask().get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Interrupted");
        } catch (ExecutionException e) {
            log.error("[platform/cpu-task] execution failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getCause().getMessage());
        }
    }

    @GetMapping("/api/thread/virtual/cpu-task")
    public ResponseEntity<String> virtualCpuTask() {
        try {
            return ResponseEntity.ok(virtualThreadService.cpuTask().get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Interrupted");
        } catch (ExecutionException e) {
            log.error("[virtual/cpu-task] execution failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getCause().getMessage());
        }
    }
}
