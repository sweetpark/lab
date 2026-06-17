package com.example.playground.jmeter.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppLockService {

    private final JdbcTemplate jdbcTemplate;

    // 애플리케이션 락(Application Lock) 전략 — MariaDB GET_LOCK() 사용.
    //
    // [동작 원리]
    // GET_LOCK('이름', 타임아웃): 지정한 이름의 사용자 정의 잠금을 획득한다.
    // 동일 이름의 락을 다른 커넥션이 보유 중이면 타임아웃까지 대기한다.
    // DB 행/테이블과 무관하게 비즈니스 로직 레벨에서 직렬화할 수 있다.
    //
    // [언제 쓰나] 여러 테이블에 걸친 복합 연산을 하나의 임계 구역으로 묶을 때.
    // Redis가 없는 환경에서 분산 락의 간단한 대안으로 사용한다.
    //
    // [주의] GET_LOCK은 커넥션 단위로 동작한다. HikariCP처럼 커넥션 풀을 사용할 때
    // 락 획득과 해제가 반드시 같은 커넥션에서 이루어져야 한다.
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        String lockName = "product_stock_lock_" + productId;

        // 타임아웃 5초: 5초 내 락을 못 얻으면 0(실패) 반환
        Integer acquired = jdbcTemplate.queryForObject(
                "SELECT GET_LOCK(?, 5)",
                Integer.class,
                lockName
        );

        if (acquired == null || acquired != 1) {
            throw new IllegalStateException("락 획득 실패: " + lockName);
        }

        try {
            Integer stock = jdbcTemplate.queryForObject(
                    "SELECT stock FROM product WHERE id = ?",
                    Integer.class,
                    productId
            );

            if (stock == null || stock < amount) {
                throw new IllegalStateException("재고 부족: 현재=" + stock + ", 요청=" + amount);
            }

            jdbcTemplate.update(
                    "UPDATE product SET stock = stock - ? WHERE id = ?",
                    amount, productId
            );

        } finally {
            // 파라미터 바인딩으로 변경: 문자열 직접 연결은 교육용 코드에서도 나쁜 패턴이다.
            // queryForObject로 RELEASE_LOCK 결과(1=성공, 0=실패)를 확인한다.
            try {
                Integer released = jdbcTemplate.queryForObject("SELECT RELEASE_LOCK(?)", Integer.class, lockName);

                if (released == null || released != 1) {
                    log.warn("GET_LOCK 해제 실패 또는 이미 해제됨: lockName={}", lockName);
                }

            } catch (Exception unlockEx) {
                log.error("RELEASE_LOCK 실행 중 오류: lockName={}", lockName, unlockEx);
            }
        }
    }
}
