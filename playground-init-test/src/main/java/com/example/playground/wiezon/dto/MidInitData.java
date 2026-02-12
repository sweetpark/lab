package com.example.playground.wiezon.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MidInitData {
    String mid;
    String cono;
    String gid;
    String l1Vid;
    
    //현금영수증
    String crctPtnCd;
    String crctCpid;
    String crctKeyType;
    String crctKey;

    CpidMap cpidMap;
    List<PayData> payDataList;

    @Override
    public String toString() {
        return "InitData{" +
                "mid='" + mid + '\'' +
                ", cono='" + cono + '\'' +
                ", gid='" + gid + '\'' +
                ", l1Vid='" + l1Vid + '\'' +
                ", crctPtnCd='" + crctPtnCd + '\'' +
                ", crctCpid='" + crctCpid + '\'' +
                ", crctKeyType='" + crctKeyType + '\'' +
                ", crctKey='" + crctKey + '\'' +
                ", cpidMap=" + cpidMap +
                ", payDataList=" + payDataList +
                '}';
    }

}