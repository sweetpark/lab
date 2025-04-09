package com.example.application.fileInputOutput.entity.repository;

import com.example.application.fileInputOutput.entity.FileStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<FileStore, Long> {
}
