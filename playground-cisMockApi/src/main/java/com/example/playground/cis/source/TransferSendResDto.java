package com.example.playground.cis.source;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class TransferSendResDto {
    private String totalCount;
    private String successCount;
    private String failCount;
    private String responses;
    List<Data> response;


    @Getter
    @Builder
    public static class Data{
        private String resultCd;
        private String resultMsg;
        private String id;
        private String ordNo;
        private String trdNo;
        private String trDt;
        private String trTm;
        private String bankCd;
        private String accntNo;
        private String amt;
        private String balance;
    }

    @Builder
    public TransferSendResDto(String totalCount, String successCount, String failCount, String responses, List<Data> response) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failCount = failCount;
        this.responses = responses;
        this.response = response;
    }
}
