package com.example.playground.jdbc.source.dbUtil;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConnectionConst {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public static String URL;
    public static String USERNAME;
    public static String PASSWORD;


    @PostConstruct
    public void init(){
        URL = url;
        USERNAME = username;
        PASSWORD = password;
    }

}


