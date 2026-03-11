package com.example.playground.cis.source;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TransferRecvResDto {
    private String resultCd;
    private String resultMsg;
    private String id;
    private String ordNo;
    private String trdNo;
    private String trDt;
    private String trTm;
    private String status;
}
