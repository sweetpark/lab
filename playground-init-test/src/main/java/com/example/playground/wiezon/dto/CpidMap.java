package com.example.playground.wiezon.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CpidMap{
    String ptnCd;
    String keyType;
    String key;

    @Override
    public String toString() {
        return "CpidMap{" +
                "ptnCd='" + ptnCd + '\'' +
                ", keyType='" + keyType + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}