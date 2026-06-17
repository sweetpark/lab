package com.example.playground.jmeter.lock.repository;

import com.example.playground.jmeter.crud.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LockProductRepository extends JpaRepository<Product, Long> {

    // 비관적 쓰기 락: 조회 시점에 DB 행에 배타적 잠금을 건다(SELECT ... FOR UPDATE).
    // 다른 트랜잭션은 이 트랜잭션이 커밋 또는 롤백될 때까지 해당 행에 접근할 수 없다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);
}
