package com.example.playground.thread.service.db;

import com.example.playground.thread.entity.Product;
import com.example.playground.thread.mapper.OptimisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

/**
 * 낙관적 락
 */
@Service
@RequiredArgsConstructor
public class OptimisticService {

    private final OptimisticMapper mapper;
    private final OptimisticTxService txService;

    public void decreaseWithRetry(Long id){

        int maxRetry = 5;

        for (int attempt = 0; attempt < maxRetry; attempt++) {
            try {
                txService.decreaseOnce(id);
                return;
            } catch (OptimisticLockingFailureException e) {

                if (attempt == maxRetry - 1) {
                    throw new RuntimeException("재시도 초과");
                }

                try {
                    Thread.sleep(10L + (attempt + 1)); // backoff 추가
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public Product getProduct(Long id){
        return mapper.selectById(id);
    }
}
