package com.example.playground.jmeter.thread.safety.service.safe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SafeStatelessService {

    // [해결책 5] 무상태(Stateless) 설계 — 가장 이상적인 방법.
    //
    // 인스턴스 필드에 상태를 두지 않는다.
    // 모든 데이터는 메서드 파라미터로 받고, 결과는 반환값으로 전달한다.
    // Spring @Service 빈을 싱글톤으로 설계하는 올바른 방법이다.
    //
    // 스레드마다 스택 프레임이 분리되므로 메서드 지역 변수는 자동으로 스레드 안전하다.
    public String process(String requestId, int value) {
        // 지역 변수: 각 스레드의 스택에 독립적으로 생성되므로 공유되지 않는다.
        int localResult = value * 2;

        return "requestId=" + requestId + ", result=" + localResult;
    }
}
