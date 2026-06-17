package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnsafeSingletonFieldService {

    // [문제 케이스] Spring 싱글톤 빈(Bean)의 인스턴스 필드 공유.
    //
    // Spring @Service는 기본적으로 싱글톤이다.
    // 즉, 애플리케이션 전체에서 이 빈의 인스턴스가 하나만 존재한다.
    // 여러 HTTP 요청(스레드)이 동시에 이 빈을 호출하면 requestCount 필드를 공유한다.
    //
    // [흔한 실수] 요청 단위로 상태를 저장하려고 인스턴스 필드를 쓰는 경우.
    // 올바른 해결책: 상태를 메서드 지역 변수나 ThreadLocal에 두거나, 무상태 설계를 따른다.
    private int requestCount = 0;

    private String lastRequestId = "";

    public String process(String requestId) {
        requestCount++;
        lastRequestId = requestId;

        // 다른 스레드가 끼어들어 lastRequestId를 바꾸면 여기서 엉뚱한 값이 출력된다.
        return "처리완료. 총요청수=" + requestCount + ", 마지막요청=" + lastRequestId;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void reset() {
        requestCount = 0;
        lastRequestId = "";
    }
}
