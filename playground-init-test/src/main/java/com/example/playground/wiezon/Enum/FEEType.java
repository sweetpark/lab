package com.example.playground.wiezon.Enum;

public enum FEEType {

    MICRO("1", "2", "A1"),
    SMALL1("2", "2", "B1"),
    SMALL2("3", "2", "B2"),
    SMALL3("4", "2", "B3"),
    GENERAL("5", "2", "00");

    public final String feeRate;
    public final String feeType;
    public final String SMMbsCd;

    FEEType(String feeRate, String feeType, String SMMbsCd) {
        this.feeRate = feeRate;
        this.feeType = feeType;
        this.SMMbsCd = SMMbsCd;
    }
}
