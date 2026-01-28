package com.example.playground.wiezon.dto;

import java.util.List;
import java.util.Map;

public class MetaData {

    private String table;
    private List<Map<String, Object>> rows;

    public String getTable() {
        return table;
    }
    public List<Map<String,Object>> getRows() {
        return rows;
    }
}
