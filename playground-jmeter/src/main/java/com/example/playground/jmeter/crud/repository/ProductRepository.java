package com.example.playground.jmeter.crud.repository;

import com.example.playground.jmeter.crud.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
