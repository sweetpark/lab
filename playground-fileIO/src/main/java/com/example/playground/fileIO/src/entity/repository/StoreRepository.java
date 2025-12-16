package com.example.playground.fileIO.src.entity.repository;

import com.example.playground.fileIO.src.entity.FileStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<FileStore, Long> {
}
