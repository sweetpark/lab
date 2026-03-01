package com.example.playground.wiezon.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PayData {
    private String pmCd;      // 결제수단 코드 (예: 01)
    private String spmCd;     // 하위 결제수단 코드 (예: 01)

    // 세트 1: 승인-부분취소 (OTID 관계)
    private String tid1;      // 원거래 승인 TID
    private String tid1P1;    // 부분취소 1
    private String tid1P2;    // 부분취소 2
    private String tid1P3;    // 부분취소 3

    // 세트 2: 승인-전취소 (TID 동일)
    private String tid2;      // 승인 및 전취소 공통 TID

    private String appNo1; // 부분취소용 승인번호
    private String appNo2; // 승인용 승인번호
    private String appNo3; // 전취소용 승인번호
}
