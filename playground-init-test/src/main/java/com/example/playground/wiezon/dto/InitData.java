package com.example.playground.wiezon.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InitData {

    List<CpidMap> cpidList = new ArrayList<>();
    List<MidInitData> midList = new ArrayList<>();

    public void addCpidList(CpidMap cpidMap){
        this.cpidList.add(cpidMap);
    }
    public void addMidList(MidInitData midInitData){
        this.midList.add(midInitData);
    }
}
