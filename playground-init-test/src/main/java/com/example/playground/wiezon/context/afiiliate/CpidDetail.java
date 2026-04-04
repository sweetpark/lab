package com.example.playground.wiezon.context.afiiliate;

import com.example.playground.wiezon.Enum.FEEType;
import lombok.Getter;

import java.util.List;

@Getter
public class CpidDetail {
    public String ptnType;
    public String pmCd;
    public String spmCd;
    public String memo;
    public List<FEEType> feeTypes;

    public CpidDetail(String ptnType, String pmCd, String spmCd, String memo, List<FEEType> feeTypes) {
        this.ptnType  = ptnType;
        this.pmCd     = pmCd;
        this.spmCd    = spmCd;
        this.memo     = memo;
        this.feeTypes = feeTypes;
    }

    public String getSpmCd() {
        return spmCd;
    }
    public String getPtnType() {
        return ptnType;
    }
    public String getPmCd() {
        return pmCd;
    }

    public String getMemo() {
        return memo;
    }
    public List<FEEType> getFeeTypes() {
        return feeTypes;
    }
}
