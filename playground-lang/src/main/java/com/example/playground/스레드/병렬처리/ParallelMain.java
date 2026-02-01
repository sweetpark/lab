package com.example.playground.스레드.병렬처리;

import java.util.stream.LongStream;

public class ParallelMain {
    public static void main(String[] args) {

        // 상황: 1부터 10억까지 더하는 엄청난 연산이 필요함.

        long start = System.currentTimeMillis();

        long sum = LongStream.rangeClosed(1, 1_000_000_000)
                // [문제 1] 이 스트림을 '병렬(멀티스레드)'로 처리하도록 바꾸는 메서드는?
                // 설명: 내부적으로 ForkJoinPool을 사용하여 CPU 코어들을 풀가동합니다.
                // 주의: 데이터가 적거나, IO 작업(DB, 파일)이 있는 경우에는 오히려 느려질 수 있습니다.
                .parallel()
                .sum();

        long end = System.currentTimeMillis();

        System.out.println("결과: " + sum);
        System.out.println("걸린 시간: " + (end - start) + "ms");
    }
}