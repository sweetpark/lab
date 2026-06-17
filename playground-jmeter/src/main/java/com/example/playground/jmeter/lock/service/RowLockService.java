package com.example.playground.jmeter.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RowLockService {

    private final JdbcTemplate jdbcTemplate;

    // 행 락(Row Lock) 전략 — JDBC 직접 사용.
    //
    // [비관적 락과의 차이]
    // 비관적 락(PessimisticLockService)은 JPA @Lock 어노테이션으로 처리한다.
    // 행 락은 동일한 SELECT ... FOR UPDATE를 JDBC로 직접 실행해 JPA 캐시를 우회한다.
    // 결과는 비관적 락과 동일하지만, 이 방식은 JPA 없이 MyBatis/순수 JDBC 환경에서 쓰인다.
    // JMeter에서 두 엔드포인트의 응답시간을 비교해 JPA 오버헤드를 측정할 수 있다.
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        // FOR UPDATE: InnoDB의 행 수준 잠금. 동일 행에 대한 다른 트랜잭션의 쓰기를 대기시킨다.
        Integer stock = jdbcTemplate.queryForObject(
                "SELECT stock FROM product WHERE id = ? FOR UPDATE",
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
    }
}
