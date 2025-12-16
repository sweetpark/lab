package com.example.playground.fileIO.source.entity.repository;

import com.example.playground.fileIO.source.entity.FileStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<FileStore, Long> {
}
