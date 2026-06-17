package com.example.playground.jmeter.crud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer price;

    // 락 경합 테스트의 핵심 자원. 여러 스레드가 동시에 이 값을 차감하려 할 때 어떤 일이 벌어지는지 관찰한다.
    private Integer stock;

    // 낙관적 락(Optimistic Lock)의 버전 컬럼.
    // JPA는 UPDATE 시 WHERE version = :현재버전 조건을 자동으로 추가한다.
    // 다른 트랜잭션이 먼저 수정해 version이 바뀌었다면 UPDATE 결과가 0건이 되고 OptimisticLockException을 던진다.
    @Version
    private Long version;

    private LocalDateTime createdAt;

    @Builder
    public Product(String name, Integer price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
    }

    public void decreaseStock(int amount) {
        if (this.stock == null || this.stock < amount) {
            throw new IllegalStateException("재고 부족: 현재 재고=" + this.stock + ", 요청=" + amount);
        }

        this.stock -= amount;
    }

    public void update(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}
