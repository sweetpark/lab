package com.example.playground.jmeter.crud.dto;

import com.example.playground.jmeter.crud.entity.Product;
import java.time.LocalDateTime;

// version 필드를 응답에 포함하는 이유:
// JMeter 부하 테스트 결과를 분석할 때, 동시 요청이 들어올수록 version 값이 얼마나 증가하는지
// 확인함으로써 낙관적 락의 충돌 빈도를 직접 관찰하기 위함이다.
public record ProductResponse(
        Long id,
        String name,
        Integer price,
        Integer stock,
        Long version,
        LocalDateTime createdAt
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getVersion(),
                product.getCreatedAt()
        );
    }
}
