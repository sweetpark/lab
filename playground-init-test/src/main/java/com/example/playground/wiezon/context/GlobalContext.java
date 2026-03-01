package com.example.playground.wiezon.context;

import lombok.Getter;

@Getter
public class GlobalContext {
    private final String cono;
    private final String gid;
    private final String l1Vid;

    //현금영수증
    private final String crctPtnCd;
    private final String crctCpid;
    private final String crctKeyType;
    private final String crctKey;

    public GlobalContext(String cono, String gid, String l1Vid,
                         String crctPtnCd, String crctCpid, String crctKeyType, String crctKey) {
        this.cono = cono;
        this.gid = gid;
        this.l1Vid = l1Vid;
        this.crctPtnCd = crctPtnCd;
        this.crctCpid = crctCpid;
        this.crctKeyType = crctKeyType;
        this.crctKey = crctKey;
    }

    @Override
    public String toString() {
        return "GlobalContext{" +
                ", cono='" + cono + '\'' +
                ", gid='" + gid + '\'' +
                ", l1Vid='" + l1Vid + '\'' +
                ", crctPtnCd='" + crctPtnCd + '\'' +
                ", crctCpid='" + crctCpid + '\'' +
                ", crctKeyType='" + crctKeyType + '\'' +
                ", crctKey='" + crctKey + '\'' +
                '}';
    }
}
