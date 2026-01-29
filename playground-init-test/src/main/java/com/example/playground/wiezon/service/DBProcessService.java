package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DBProcessService {

    @Autowired
    DataSource dataSource;

    public void save(MetaData metaData){

        try(Connection con = dataSource.getConnection()){
            try{
                con.setAutoCommit(false);

                dbInsert(metaData,con);
                con.commit();
            }catch(Exception e){
                con.rollback();
                throw new RuntimeException("DB Insert 실패", e);
            }
        }catch(Exception e){
            throw new RuntimeException("DB Connection 실패",e);
        }



    }

    private static void dbInsert(MetaData metaData, Connection con) {
        String table = metaData.getTable();

        for(Map<String, Map<String,Object>> row : metaData.getRows()){

            List<String> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            row.forEach((column, map) -> {
                columns.add(column);
                values.add(map.get("value"));
            });

            String sql = "INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES ("
                    + values.stream().map(map -> "?").collect(Collectors.joining(", ")) +")";


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

}
