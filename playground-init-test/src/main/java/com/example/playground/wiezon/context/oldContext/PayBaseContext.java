package com.example.playground.wiezon.context.oldContext;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PayBaseContext {
    private String vanCd;
    private String mid;
    private String gid;
    private String vid;


    public PayBaseContext(MidContext midContext) {
        this.vanCd = midContext.getCpidContext().getCertPtnCd();
        this.gid = midContext.getGid();
        this.mid = midContext.getMid();
        this.vid = midContext.getL1Vid();
    }
    public PayBaseContext(String vanCd, String mid, String gid, String vid) {
        this.vanCd = vanCd;
        this.mid = mid;
        this.gid = gid;
        this.vid = vid;
    }
}
