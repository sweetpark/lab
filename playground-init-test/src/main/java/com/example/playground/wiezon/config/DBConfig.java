package com.example.playground.wiezon.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource(){
        DataSource dataSource = new HikariDataSource();
        ((HikariDataSource) dataSource).setJdbcUrl(url);
        ((HikariDataSource) dataSource).setUsername(username);
        ((HikariDataSource) dataSource).setPassword(password);

        return dataSource;
    }
}
