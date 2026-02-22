package com.example.playground.thread.service.db;

import com.example.playground.thread.entity.Product;
import com.example.playground.thread.mapper.OptimisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [낙관적 락]
 * 같은 클래스 내부 Transactional 불가 >> 클래스 분리
 */
@Service
@RequiredArgsConstructor
public class OptimisticTxService {

    private final OptimisticMapper mapper;


    @Transactional
    public void decreaseOnce(Long id) {

        Product product = mapper.selectById(id);

        if (product.getQuantity() <= 0) {
            throw new RuntimeException("재고 부족");
        }

        int updated = mapper.updateWithVersion(id, product.getVersion());

        if (updated == 0) {
            throw new OptimisticLockingFailureException("낙관적 락 충돌");
        }
    }

}
