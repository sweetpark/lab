package com.example.playground.wiezon.Enum;

import java.util.List;

public enum CPIDType {
    CERT("00", "01", "01", "인증", List.of(FEEType.GENERAL, FEEType.MICRO, FEEType.SMALL1, FEEType.SMALL2, FEEType.SMALL3)),
    OLD_CERT("00", "01", "02", "구인증", List.of(FEEType.GENERAL, FEEType.MICRO, FEEType.SMALL1, FEEType.SMALL2, FEEType.SMALL3)),
    OFFLINE("00", "01", "03", "오프라인", List.of(FEEType.GENERAL, FEEType.MICRO, FEEType.SMALL1, FEEType.SMALL2, FEEType.SMALL3)),
    NO_CERT("00", "01", "05", "비인증", List.of(FEEType.GENERAL, FEEType.MICRO, FEEType.SMALL1, FEEType.SMALL2, FEEType.SMALL3));

    public final String ptnType;
    public final String pmCd;
    public final String spmCd;
    public final String memo;
    public final List<FEEType> feeTypes;

    CPIDType(String ptnType, String pmCd, String spmCd, String memo, List<FEEType> feeTypes) {
        this.ptnType  = ptnType;
        this.pmCd     = pmCd;
        this.spmCd    = spmCd;
        this.memo     = memo;
        this.feeTypes = feeTypes;
    }
}
