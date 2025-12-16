package com.example.playground.jdbc.source.dbUtil;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection(){
        try{
            Connection connection = DriverManager.getConnection(
                        ConnectionConst.URL,
                        ConnectionConst.USERNAME,
                        ConnectionConst.PASSWORD
            );
            return connection;

        }catch( SQLException e){
            log.error("[JDBC] Connection Error ! : {}", e.getMessage());
            throw new RuntimeException("Connection Error!", e);
        }

    }

}
