package com.example.playground.wiezon.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MidInitData {
    String mid;
    String cono;
    String gid;
    String l1Vid;
    CpidMap cpidMap;

    @Override
    public String toString() {
        return "InitData{" +
                "mid='" + mid + '\'' +
                ", cono='" + cono + '\'' +
                ", cpidMap=" + cpidMap +
                '}';
    }

}