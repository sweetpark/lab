package com.example.playground.jmeter.thread.safety.service.unsafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UnsafeListService {

    // [문제 케이스] 공유 ArrayList에 여러 스레드가 동시에 add().
    //
    // ArrayList는 내부 배열(Object[])의 크기가 가득 차면 더 큰 배열로 복사(grow)한다.
    // 두 스레드가 동시에 grow 타이밍에 진입하면 배열 인덱스가 충돌해 null 원소가 생기거나
    // ArrayIndexOutOfBoundsException / 데이터 유실이 발생한다.
    private final List<String> sharedList = new ArrayList<>();

    public void add(String value) {
        sharedList.add(value);
    }

    public int size() {
        return sharedList.size();
    }

    public void reset() {
        sharedList.clear();
    }
}
