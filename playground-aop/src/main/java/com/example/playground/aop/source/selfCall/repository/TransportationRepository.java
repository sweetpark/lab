package com.example.playground.aop.source.selfCall.repository;

import com.example.playground.aop.source.selfCall.entity.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {
}
