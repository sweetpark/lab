package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;

import java.util.Map;

public interface MetaDataProcessStrategy {
    boolean supports(MetaData metaData);
    void process(MetaData template, InitData propertiesData);

    default boolean isTemplateMatched(Map<String, Map<String, Object>> row, String colKey, String templateStr) {
        if (!row.containsKey(colKey) || row.get(colKey) == null) return false;

        Object valueObj = row.get(colKey).get("value");
        return valueObj != null && valueObj.equals(templateStr);
    }
}
