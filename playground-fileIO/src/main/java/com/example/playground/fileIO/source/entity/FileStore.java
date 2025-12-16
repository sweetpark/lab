package com.example.playground.fileIO.source.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class FileStore {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column
    private String uploadFileName;
    @Column
    private String storeFileName;

    @Builder
    public FileStore(String uploadFileName, String storeFileName){
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
