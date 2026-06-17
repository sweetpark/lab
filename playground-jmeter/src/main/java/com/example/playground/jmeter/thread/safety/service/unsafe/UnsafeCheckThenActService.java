package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UnsafeCheckThenActService {

    // [문제 케이스] Check-Then-Act 패턴의 비원자성.
    //
    // if (stock > 0) { stock-- } 는 두 단계로 구성된 복합 연산이다.
    // 스레드 A와 B가 동시에 if 조건을 통과하면, 둘 다 stock--를 실행해 stock이 음수가 된다.
    //
    //   [타임라인]
    //   Thread A: stock=1 확인(조건 true) → (컨텍스트 스위치)
    //   Thread B: stock=1 확인(조건 true) → stock-- → stock=0
    //   Thread A: stock-- → stock=-1  ← 음수 재고 발생!
    //
    // DB 락 없이 애플리케이션 레벨에서만 처리할 때 이 문제가 발생한다.
    private int stock = 100;

    public String decreaseStock() {
        if (stock > 0) {
            stock--;
            return "차감 성공. 남은 재고=" + stock;
        }

        return "재고 없음";
    }

    public int getStock() {
        return stock;
    }

    public void reset() {
        stock = 100;
    }
}
