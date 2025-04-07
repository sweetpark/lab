package com.example.application.paging;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
public class Temperature {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String temperature;
    private String name;
    private String createTime;
    private String updateTime;

    public Temperature(){}

    public Temperature(String temperature, String name){
        this.temperature = temperature;
        this.name = name;
        this.createTime = LocalDateTime.now().format(formatter);
        this.updateTime = LocalDateTime.now().format(formatter);
    }


    public void update(String temperature, String name){
        this.temperature = temperature;
        this.name = name;
        this.updateTime = LocalDateTime.now().format(formatter);
    }




}
