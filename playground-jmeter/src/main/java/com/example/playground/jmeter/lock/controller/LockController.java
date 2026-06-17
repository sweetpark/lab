package com.example.playground.jmeter.lock.controller;

import com.example.playground.jmeter.lock.dto.StockRequest;
import com.example.playground.jmeter.lock.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products/{id}/stock")
@RequiredArgsConstructor
public class LockController {

    private final OptimisticLockService optimisticLockService;
    private final PessimisticLockService pessimisticLockService;
    private final RowLockService rowLockService;
    private final TableLockService tableLockService;
    private final AppLockService appLockService;

    // 낙관적 락: 충돌 시 409 Conflict 반환. JMeter에서 오류율로 경합 빈도를 측정한다.
    @PutMapping("/optimistic")
    public ResponseEntity<String> optimistic(@PathVariable Long id, @RequestBody @Valid StockRequest request) {
        try {
            optimisticLockService.decreaseStock(id, request.amount());
            return ResponseEntity.ok("낙관적 락 성공");

        } catch (OptimisticLockingFailureException e) {
            log.warn("낙관적 락 충돌 발생: productId={}", id);
            return ResponseEntity.status(409).body("충돌 발생 - 다른 트랜잭션이 먼저 수정함");
        }
    }

    @PutMapping("/pessimistic")
    public ResponseEntity<String> pessimistic(@PathVariable Long id, @RequestBody @Valid StockRequest request) {
        pessimisticLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("비관적 락 성공");
    }

    @PutMapping("/row-lock")
    public ResponseEntity<String> rowLock(@PathVariable Long id, @RequestBody @Valid StockRequest request) {
        rowLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("행 락 성공");
    }

    @PutMapping("/table-lock")
    public ResponseEntity<String> tableLock(@PathVariable Long id, @RequestBody @Valid StockRequest request) {
        tableLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("테이블 락 성공");
    }

    @PutMapping("/app-lock")
    public ResponseEntity<String> appLock(@PathVariable Long id, @RequestBody @Valid StockRequest request) {
        appLockService.decreaseStock(id, request.amount());
        return ResponseEntity.ok("애플리케이션 락 성공");
    }
}
