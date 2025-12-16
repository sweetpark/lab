package com.example.playground.crolling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrollingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrollingApplication.class, args);
        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver.exe");
    }
}
