package com.example.playground.wiezon.util;

import java.util.Map;

public class DataVariableResolver {

    /**
     * row의 모든 컬럼을 순회하며, 값이 ${VARIABLE} 패턴인 경우 variables 맵에서 찾아 치환합니다.
     * @param row JSON에서 읽어온 데이터 row
     * @param variables 치환할 변수 맵 (key: "${VAR}", value: "actualValue")
     */
    public static void replace(Map<String, Map<String, Object>> row, Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            row.keySet().forEach(
                    key -> {
                        System.out.println(row.get(key).toString() + " 변환 할 수 없습니다. ");
                    }
            );
            throw new RuntimeException("변환 오류");
        }

        row.forEach((colName, valueMap) -> {
            Object valueObj = valueMap.get("value");
            if (valueObj instanceof String strValue) {
                if (strValue.startsWith("${") && strValue.endsWith("}")) {
                    String replacement = variables.get(strValue);
                    if (replacement != null) {
                        valueMap.put("value", replacement);
                    }else{
                        throw new RuntimeException("존재하지 않는 strValue : " + strValue);
                    }
                }
            }
        });
    }
}
