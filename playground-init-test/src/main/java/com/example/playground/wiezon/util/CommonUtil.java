package com.example.playground.wiezon.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
    public static @NonNull Map<String, Object> valueMap(String now) {
        Map<String, Object> newValue = new HashMap<>();
        newValue.put("value", now);
        return newValue;
    }

    public static @NonNull Map<String, Map<String, Object>> createNewRow(Map<String, Map<String, Object>> templateRow) {
        Map<String, Map<String, Object>> newRow = new HashMap<>();

        for (String key : templateRow.keySet()) {
            Map<String, Object> innerMap = templateRow.get(key);
            newRow.put(key, new HashMap<>(innerMap));
        }
        return newRow;
    }
}
