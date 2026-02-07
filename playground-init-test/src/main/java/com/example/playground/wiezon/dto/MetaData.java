package com.example.playground.wiezon.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON 파일에서 파싱된 테이블 데이터를 담는 DTO 클래스입니다.
 * division: 데이터 분류 (MID, GID, CONTRACT 등)<br>
 * table: 대상 DB 테이블명<br>
 * rows: 실제 데이터 행 리스트
 */
public class MetaData {

    private String division;
    private String table;
    private List<Map<String, Map<String, Object>>> rows;
    private static final Set<String> ALLOWED_TABLES =
            Set.of(
                    "TEST_TABLE",
                    "TBSI_PTN_CPID","TBSI_PTN_FEE",
                    "TBSI_CO","TBSI_CO_PAY","TBSI_STMT_FEE",
                    "TBCS_FAQ","TBCS_NOTICE",
                    "TBAD_RULE_USR","TBSI_GRP","TBSI_USR","TBSI_MBS","TBSI_MBS_PTN_LNK","TBSI_MBS_SVC",
                    "TBSI_CHG_HIST","TSBI_MBS_SVC","TBSI_STMT_CYCLE","TBIS_STMT_FEE",
                    "TBSI_MBS_KEY", "TBSI_STMT_SVC",
                    "TBSI_ADDSVC_FEE",
                    "TBSI_MBS_MEMO",
                    "TBSI_VGRP"
            );



    public String getDivision(){
        if(this.division == null){
            throw new RuntimeException("Invalid division: " + this);
        }
        return this.division;
    }

    public String getTable() {
        if(!isAllowedTable()){
            throw new RuntimeException("Invalid table: " + this.table);
        }
        return this.table;
    }

    public MetaData(String table, List<Map<String, Map<String,Object>>> rows){
        this.table = table;
        this.rows = rows;
    }

    public MetaData(String division, String table, List<Map<String, Map<String,Object>>> rows){
        this.division = division;
        this.table = table;
        this.rows = rows;
    }

    public List<Map<String,Map<String,Object>>> getRows() {
        return this.rows;
    }

    private boolean isAllowedTable() {
        return ALLOWED_TABLES.contains(this.table);
    }

}