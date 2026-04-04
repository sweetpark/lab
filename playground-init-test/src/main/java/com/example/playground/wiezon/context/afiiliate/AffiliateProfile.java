package com.example.playground.wiezon.context.afiiliate;

import com.example.playground.wiezon.Enum.CPIDType;
import com.example.playground.wiezon.context.Profile;

import java.util.ArrayList;
import java.util.List;

public class AffiliateProfile implements Profile {
    private final String ptnCd;
    private final String ptnCpid;
    private final String secretKey;
    private final List<CpidDetail> cpids = new ArrayList<>();

    public AffiliateProfile(String ptnCd, String ptnCpid, String secretKey) {
        this.ptnCd     = ptnCd;
        this.ptnCpid   = ptnCpid;
        this.secretKey = secretKey;
    }

    public void addCpidType(CPIDType type) {
        this.cpids.add(new CpidDetail(type.ptnType, type.pmCd, type.spmCd, type.memo, type.feeTypes));
    }


    public String getPtnCd() {
        return ptnCd;
    }

    public String getPtnCpid() {
        return ptnCpid;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public List<CpidDetail> getCpids() {
        return cpids;
    }
}
