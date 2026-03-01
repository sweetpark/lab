package com.example.playground.wiezon.context;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 초기화 작업에 필요한 전체 데이터를 담는 루트 DTO 클래스입니다.
 * CPID 정보 리스트와 MID 초기화 정보 리스트를 포함합니다.
 */
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