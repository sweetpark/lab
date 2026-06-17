package com.example.playground.jmeter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadConfig {

    // 가상 스레드 전용 ExecutorService.
    //
    // Executors.newVirtualThreadPerTaskExecutor():
    //   - 작업마다 새로운 가상 스레드를 생성한다.
    //   - 가상 스레드는 JVM이 관리하는 경량 스레드로, OS 스레드와 1:1 매핑되지 않는다.
    //   - I/O 블로킹 시 캐리어 스레드(OS 스레드)를 반납하고 다른 가상 스레드가 실행된다.
    //   - 수만 개를 동시에 생성해도 OS 스레드를 그만큼 소비하지 않는다.
    //   - Spring 컨텍스트 종료 시 shutdown() 자동 호출
    @Bean(name = "virtualThreadExecutor", destroyMethod = "shutdown")
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    // 비교 기준이 되는 플랫폼 스레드 풀.
    //
    // Executors.newFixedThreadPool(200):
    //   - OS 스레드 200개를 미리 생성한다.
    //   - 스레드가 I/O로 블로킹되면 해당 OS 스레드는 아무것도 못 하고 대기한다.
    //   - 200개 초과 요청은 큐에서 대기한다.
    //   - Spring 컨텍스트 종료 시 shutdown() 자동 호출
    @Bean(name = "platformThreadExecutor", destroyMethod = "shutdown")
    public ExecutorService platformThreadExecutor() {
        return Executors.newFixedThreadPool(200);
    }
}
