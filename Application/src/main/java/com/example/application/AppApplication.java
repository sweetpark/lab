package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//통합실행 모듈
@SpringBootApplication(scanBasePackages = {
        "com.example.application"
//        ,"com.example.playground.aop"
//        ,"com.example.playground.cache"
//        ,"com.example.playground.crolling"
//        ,"com.example.playground.exception"
//        ,"com.example.playground.exterior"
//        ,"com.example.playground.fileIO"
//        ,"com.example.playground.noti"
//        ,"com.example.playground.page"
//        ,"com.example.playground.jdbc"
//        ,"com.example.playground.validation"
        ,"com.example.playground.batch"
})
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
