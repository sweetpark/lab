package com.example.playground.wiezon._enum;

public enum CryptoType {
    NONE,ENC_HASH, OTP, PASSWORD, ENC_BASE64;


    public static CryptoType from(Object value) {
        if (value == null) {
            return NONE;
        }
        try {
            return CryptoType.valueOf(value.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported crypto type: " + value);
        }
    }
}
