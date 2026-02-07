package com.example.playground.wiezon.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 프로젝트 전반에서 사용되는 공통 유틸리티 클래스입니다.
 */
public class CommonUtil {

    /**
     * 주어진 문자열을 "value" 키를 가진 맵 형태로 래핑하여 반환합니다.
     * 예: "test" -> {"value": "test"}
     *
     * @param now 래핑할 문자열 값
     * @return 래핑된 맵 객체
     */
    public static @NonNull Map<String, Object> valueMap(String now) {
        Map<String, Object> newValue = new HashMap<>();
        newValue.put("value", now);
        return newValue;
    }

    /**
     * 템플릿 행(Row)을 깊은 복사(Deep Copy)하여 새로운 행 객체를 생성합니다.
     * 원본 템플릿의 손상을 방지하기 위해 사용됩니다.
     *
     * @param templateRow 복사할 원본 행
     * @return 복사된 새로운 행
     */
    public static @NonNull Map<String, Map<String, Object>> createNewRow(Map<String, Map<String, Object>> templateRow) {
        Map<String, Map<String, Object>> newRow = new HashMap<>();

        for (String key : templateRow.keySet()) {
            Map<String, Object> innerMap = templateRow.get(key);
            newRow.put(key, new HashMap<>(innerMap));
        }
        return newRow;
    }
}