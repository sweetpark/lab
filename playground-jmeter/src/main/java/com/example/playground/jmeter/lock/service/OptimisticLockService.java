package com.example.playground.jmeter.lock.service;

import com.example.playground.jmeter.crud.entity.Product;
import com.example.playground.jmeter.lock.repository.LockProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimisticLockService {

    private final LockProductRepository lockProductRepository;

    // 낙관적 락(Optimistic Lock) 전략.
    //
    // [동작 원리]
    // 1. 트랜잭션 A, B가 동시에 같은 상품을 조회 → 둘 다 version=5를 읽는다.
    // 2. A가 먼저 UPDATE 성공 → DB의 version이 6으로 증가한다.
    // 3. B가 UPDATE 시도: WHERE version=5 조건이 맞지 않아 수정된 행이 0건 → JPA가 OptimisticLockException을 던진다.
    //
    // [언제 쓰나] 충돌이 드문 환경(읽기 위주). 충돌 시 재시도 비용이 발생하므로
    // 충돌이 잦은 환경에서는 비관적 락보다 오히려 느릴 수 있다.
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        Product product = lockProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: id=" + productId));

        // OptimisticLockException은 트랜잭션 커밋 시점에 발생한다.
        // 여기서는 재시도 없이 바로 예외를 전파해 JMeter에서 오류율로 관찰한다.
        product.decreaseStock(amount);
    }
}
