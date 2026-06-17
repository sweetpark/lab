package com.example.playground.jmeter.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableLockService {

    private final JdbcTemplate jdbcTemplate;

    // 테이블 락(LOCK TABLES ... WRITE) 전략.
    //
    // 테이블 전체를 잠가 다른 모든 세션의 읽기/쓰기를 차단한다.
    // 낙관적/비관적 락이 행 단위인 것과 달리, 테이블 전체를 직렬화하므로 동시성이 가장 낮다.
    //
    // 핵심 제약 — LOCK TABLES는 세션(커넥션) 단위 동작:
    //   - LOCK과 UNLOCK이 반드시 동일 커넥션에서 실행되어야 한다.
    //   - @Transactional을 걸면 Spring이 트랜잭션 시작 전에 암묵적 UNLOCK을 실행해
    //     락이 즉시 해제되는 충돌이 발생한다.
    //   - 따라서 @Transactional 대신 ConnectionCallback으로 하나의 커넥션을 직접 점유한다.
    public void decreaseStock(Long productId, int amount) {
        jdbcTemplate.execute((Connection conn) -> {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                // 테이블 전체 WRITE 락 획득: 다른 세션은 읽기/쓰기 모두 대기
                stmt.execute("LOCK TABLES product WRITE");

                int currentStock;

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT stock FROM product WHERE id = ?")) {
                    ps.setLong(1, productId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("상품을 찾을 수 없습니다: id=" + productId);
                        }

                        currentStock = rs.getInt("stock");
                    }
                }

                if (currentStock < amount) {
                    throw new IllegalStateException(
                            "재고 부족: current=" + currentStock + ", requested=" + amount);
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE product SET stock = stock - ? WHERE id = ?")) {
                    ps.setInt(1, amount);
                    ps.setLong(2, productId);
                    ps.executeUpdate();
                }

                // 락 해제 후 커밋
                stmt.execute("UNLOCK TABLES");
                conn.commit();

            } catch (Exception e) {
                try {
                    // 예외 발생 시에도 반드시 같은 커넥션에서 UNLOCK
                    conn.createStatement().execute("UNLOCK TABLES");
                } catch (Exception unlockEx) {
                    log.error("UNLOCK TABLES 실패 - DB 관리자 확인 필요", unlockEx);
                }

                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    log.error("rollback 실패", rollbackEx);
                }

                throw new RuntimeException(e);

            } finally {
                conn.setAutoCommit(true);
            }

            return null;
        });
    }
}
