package com.example.playground.wiezon.context;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MidContext {
    String mid;
    String cono;
    String gid;
    String l1Vid;

    //현금영수증
    String crctPtnCd;
    String crctCpid;
    String crctKeyType;
    String crctKey;
    CpidContext cpidContext;

    @Override
    public String toString() {
        return "InitData{" +
                "mid='" + mid + '\'' +
                ", cpidContext=" + cpidContext +
                '}';
    }

}