package com.example.playground.aop.src.selfCall.repository;

import com.example.playground.aop.src.selfCall.entity.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {
}
