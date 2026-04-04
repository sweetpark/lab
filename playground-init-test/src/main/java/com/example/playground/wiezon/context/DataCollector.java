package com.example.playground.wiezon.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 메모리에 여러 테이블의 데이터를 division별로 모아두는 컬렉터
 */
public class DataCollector {
    // [Division] -> [TableName -> TemplateContext] 구조
    // LinkedHashMap을 사용하여 추가된 순서(Order)를 보장합니다.
    private final Map<String, Map<String, TemplateContext>> divisionMap = new LinkedHashMap<>();

    /**
     * TemplateContext를 해당 division에 저장합니다.
     * 이미 동일한 테이블이 존재할 경우 기존 행(Rows)에 새로운 행들을 합칩니다(Merge).
     */
    public void add(TemplateContext context) {
        String division = context.getDivision();
        divisionMap.putIfAbsent(division, new LinkedHashMap<>());

        Map<String, TemplateContext> tableMap = divisionMap.get(division);
        String tableName = context.getTable();

        if (tableMap.containsKey(tableName)) {
            // 이미 테이블이 존재하면 기존의 rows 리스트에 새로운 rows를 추가하여 합칩니다.
            tableMap.get(tableName).getRows().addAll(context.getRows());
        } else {
            // 처음 들어오는 테이블이면 그대로 저장합니다.
            tableMap.put(tableName, context);
        }
    }

    /**
     * 특정 Division의 모든 테이블 데이터를 가져옵니다.
     */
    public Map<String, TemplateContext> getTablesByDivision(String division) {
        return divisionMap.getOrDefault(division, Collections.emptyMap());
    }

    /**
     * 특정 Division 내의 특정 테이블을 가져옵니다.
     */
    public TemplateContext getTable(String division, String tableName) {
        Map<String, TemplateContext> tables = divisionMap.get(division);
        return (tables != null) ? tables.get(tableName) : null;
    }

    /**
     * 모든 Division 이름을 가져옵니다.
     */
    public Set<String> getAllDivisions() {
        return divisionMap.keySet();
    }

    /**
     * 특정 Division의 데이터를 메모리에서 삭제합니다 (OOM 방지용).
     */
    public void clear(String division) {
        divisionMap.remove(division);
    }

    /**
     * 모든 데이터를 비웁니다.
     */
    public void clearAll() {
        divisionMap.clear();
    }
}
