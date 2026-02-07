package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.MetaData;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 전처리가 완료된 메타데이터를 DB에 저장하는 서비스입니다.
 */
@Service
public class DBProcessService {


    private final DataSource dataSource;

    public DBProcessService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 메타데이터를 받아 DB Insert를 수행합니다.
     */
    public void save(MetaData metaData){
        
        // 1. Connection 연결
        Connection con = DataSourceUtils.getConnection(dataSource);
        try {
            // 2. insert
            dbInsert(metaData, con);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void dbInsert(MetaData metaData, Connection con) {
        String table = metaData.getTable();

        for(Map<String, Map<String,Object>> row : metaData.getRows()){

            List<String> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            row.forEach((column, map) -> {
                columns.add(column);
                values.add(map.get("value"));
            });

            String sql = "INSERT INTO " + table + " ("
                    + columns.stream().map(this::escapeColumn).collect(Collectors.joining(", "))
                    + ") VALUES ("
                    + values.stream().map(map -> "?").collect(Collectors.joining(", "))
                    +")";

            System.out.println("[ SQL ] >>>\n" + renderSql(sql, values));

            try(PreparedStatement pstmt = con.prepareStatement(sql)){

                for (int i = 0; i < values.size(); i++) {
                    pstmt.setObject(i + 1, values.get(i));
                }

                pstmt.executeUpdate();
            }catch(Exception e){
                throw new RuntimeException(e);
            }

        }
    }


    private String escapeColumn(String column){
        return "`" + column + "`";
    }

    private String renderSql(String sql, List<Object> params) {
        StringBuilder sb = new StringBuilder();
        int paramIndex = 0;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);

            if (c == '?' && paramIndex < params.size()) {
                Object value = params.get(paramIndex++);

                if (value == null) {
                    sb.append("NULL");
                } else if (value instanceof Number) {
                    sb.append(value);
                } else {
                    sb.append("'").append(value.toString().replace("'", "''")).append("'");
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}