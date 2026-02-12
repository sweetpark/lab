package com.example.playground.wiezon._enum;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PaymentDetailType {
    // ===== 신용카드 (01) =====
    CARD_AUTH("01", "01", "신용카드", "인증"),
    CARD_MANUAL_OLD("01", "02", "신용카드", "수기(구인증)"),
    CARD_OFFLINE("01", "03", "신용카드", "오프라인"),
    CARD_BILLING("01", "04", "신용카드", "빌링"),
    CARD_MANUAL_NO_AUTH("01", "05", "신용카드", "수기(비인증)"),
    CARD_OFFLINE_VAN("01", "06", "신용카드", "오프라인(VAN)"),
    CARD_KAKAO("01", "07", "신용카드", "카카오"),

    // ===== 선불 (61) =====
    PREPAID_DEFAULT("61", "00", "선불", "선불"),
    PREPAID_MONEY("61", "01", "선불", "머니"),
    PREPAID_POINT("61", "02", "선불", "포인트");

    private final String groupCode;
    private final String detailCode;
    private final String groupName;
    private final String detailName;

    PaymentDetailType(String groupCode, String detailCode, String groupName, String detailName) {
        this.groupCode = groupCode;
        this.detailCode = detailCode;
        this.groupName = groupName;
        this.detailName = detailName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getDetailCode() {
        return detailCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDetailName() {
        return detailName;
    }

    public String getFullCode() {
        return groupCode + detailCode;
    }

    // ===== O(1) 조회용 Map =====
    private static final Map<String, PaymentDetailType> CODE_MAP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(PaymentDetailType::getFullCode, e -> e));

    public static PaymentDetailType from(String groupCode, String detailCode) {
        PaymentDetailType type = CODE_MAP.get(groupCode + detailCode);
        if (type == null) {
            throw new IllegalArgumentException("Unknown code: " + groupCode + "-" + detailCode);
        }
        return type;
    }
}
