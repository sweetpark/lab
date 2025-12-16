package com.example.playground.crolling.source;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Crolling {

    WebDriver driver = new ChromeDriver();

    public void start(){
        try {
            // 구글 홈페이지 열기
            driver.get("https://www.google.com");

            // 페이지 타이틀 출력
            String pageTitle = driver.getTitle();
            log.info("[CROLLING] 페이지 타이틀: {}", pageTitle);

            String content = driver.getPageSource();
            log.info("[CROLLING] 내용 : {}", content);

            // 잠시 대기 (예: 3초)
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 브라우저 종료
            driver.quit();
        }
    }

}
