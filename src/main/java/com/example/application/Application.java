package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		// System.setProperty("webdriver.chrome.driver", "C:/p_wy/wy_project/1.Project/cocktail/0.fork/data/chromedriver-win64/chromedriver-win64.exe");

	}

}
