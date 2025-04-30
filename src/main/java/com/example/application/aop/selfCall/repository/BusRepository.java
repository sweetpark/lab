package com.example.application.aop.selfCall.repository;

import com.example.application.aop.selfCall.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BusRepository extends JpaRepository<Bus, Long> {
    @Query(value = "INSERT INTO bus (bus_type, low_floor) VALUES (:busType, :lowFloor)", nativeQuery = true)
    void saveBus(Bus bus);
}
