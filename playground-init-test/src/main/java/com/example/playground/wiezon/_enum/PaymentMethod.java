package com.example.playground.wiezon._enum;

public enum PaymentMethod {
    CREDIT_CARD("01", "신용카드"),
    BANK_TRANSFER("02", "계좌이체"),
    VIRTUAL_ACCOUNT("03", "가상계좌"),
    CASH_RECEIPT("04", "현금영수증"),
    MOBILE("05", "휴대폰"),
    MOBILE_BILLING("06", "휴대폰빌링"),
    CREDIT_CARD_BILLING("07", "신용카드빌링"),

    ZERO_PAY_GIFT("31", "제로페이상품권"),
    CULTURE_GIFT("32", "문화상품권"),
    AFFILIATE_PREPAID("33", "제휴사 선불"),

    WECHAT("51", "위챗"),

    INTERNAL_PREPAID("61", "자체 선불"),
    EXTERNAL_MONEY_TEMP("62", "외부 머니 (임시)"),
    EXTERNAL_POINT_TEMP("63", "외부 포인트 (임시)"),
    EXTERNAL_EXCHANGE_TEMP("64", "외부 교환권 (임시)"),
    EXTERNAL_AMOUNT_TEMP("65", "외부 금액권 (임시)"),
    EXTERNAL_COUPON_TEMP("66", "외부 쿠폰 (임시)"),

    CASH("99", "현금");

    private final String code;
    private final String description;

    PaymentMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.code.equals(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment code: " + code);
    }
}
