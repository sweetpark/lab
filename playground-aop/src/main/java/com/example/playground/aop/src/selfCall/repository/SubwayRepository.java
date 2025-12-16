package com.example.playground.aop.src.selfCall.repository;

import com.example.playground.aop.src.selfCall.entity.Subway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayRepository extends JpaRepository<Subway, Long> {
}
