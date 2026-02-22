package com.example.playground.thread.service.db;

import com.example.playground.thread.entity.Product;
import com.example.playground.thread.mapper.PessimisticMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/***
 * 비관적 락
 */
@Service
@RequiredArgsConstructor
public class PessimisticService {

    private final PessimisticMapper mapper;

    @Transactional
    public void decreasePessimistic(Long id) {

        Product product = mapper.selectForUpdate(id);

        if (product.getQuantity() <= 0) {
            throw new RuntimeException("재고 부족");
        }

        mapper.updateStock(id);
    }

    public int getQuantity(Long id){
        return mapper.selectForUpdate(id).getQuantity();
    }
}
