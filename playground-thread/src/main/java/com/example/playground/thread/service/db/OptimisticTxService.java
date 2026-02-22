package com.example.playground.thread.service.db;

import com.example.playground.thread.entity.Product;
import com.example.playground.thread.mapper.OptimisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [낙관적 락 트랜잭션 전용 서비스]
 * OptimisticService의 재시도 루프 내에서 개별 트랜잭션을 실행하기 위해 별도 클래스로 분리합니다.
 *
 * ✔ 이유: 동일 클래스 내 메서드 호출 시 Spring의 AOP 프록시를 통하지 않아 @Transactional이 무시됨
 */
@Service
@RequiredArgsConstructor
public class OptimisticTxService {

    private final OptimisticMapper mapper;

    /**
     * [낙관적 락 업데이트 프로세스]
     * 1. 데이터를 먼저 읽음 (버전 정보 포함)
     * 2. 업데이트 시, 이전에 읽은 버전과 동일한지 WHERE 절에서 확인
     * 3. 영향 받은 행이 0개라면, 그 사이 다른 작업자가 수정한 것이므로 충돌(Failure) 처리
     */
    @Transactional
    public void decreaseOnce(Long id) {
        // 현재 DB의 최신 상태 읽기
        Product product = mapper.selectById(id);

        if (product.getQuantity() <= 0) {
            throw new RuntimeException("재고 부족으로 인해 차감 불가");
        }

        // DB 업데이트: WHERE id = ? AND version = ?
        int updated = mapper.updateWithVersion(id, product.getVersion());

        // 업데이트된 행이 없다면 충돌 발생으로 간주
        if (updated == 0) {
            throw new OptimisticLockingFailureException("낙관적 락 충돌 (동시 수정 발생)");
        }
    }

}
