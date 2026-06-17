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
public class PessimisticLockService {

    private final LockProductRepository lockProductRepository;

    // 비관적 락(Pessimistic Lock) 전략.
    //
    // [동작 원리]
    // SELECT ... FOR UPDATE로 행 조회 시 즉시 배타 락을 획득한다.
    // 다른 트랜잭션은 이 락이 해제될 때까지 대기한다(큐 직렬화).
    // 트랜잭션이 짧으면 TPS 감소가 적지만, 길어질수록 대기 스레드가 쌓여 응답시간이 급증한다.
    //
    // [언제 쓰나] 충돌이 잦고 데이터 정합성이 반드시 보장되어야 할 때.
    // 예: 한정판 재고 차감, 포인트 잔액 변경
    @Transactional
    public void decreaseStock(Long productId, int amount) {
        // 이 시점에 SELECT ... FOR UPDATE 실행 → 행 락 획득
        Product product = lockProductRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: id=" + productId));

        product.decreaseStock(amount);
        // 트랜잭션 커밋 시 락 해제 → 다음 대기 트랜잭션이 락 획득
    }
}
