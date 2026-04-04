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
     */
    public void add(TemplateContext context) {
        String division = context.getDivision();
        divisionMap.putIfAbsent(division, new LinkedHashMap<>());

        // 동일 Division 내에 같은 테이블이 들어올 경우 덮어쓰거나 합치는 로직 중 선택 가능
        // 여기서는 단순 보관을 위해 put을 사용합니다.
        divisionMap.get(division).put(context.getTable(), context);
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
