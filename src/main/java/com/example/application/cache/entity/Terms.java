package com.example.application.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Terms {

    private String termsName;
    private Boolean agreed;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

}
