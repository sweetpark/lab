package com.example.application.fileInputOutput.entity.repository;

import application.file.entity.FileStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<FileStore, Long> {
}
