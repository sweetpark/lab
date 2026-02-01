package com.example.playground.스레드.CAS연산;


import java.util.concurrent.atomic.AtomicInteger;

public class AtomicMain {

    // [문제 1] 일반 int 대신, 멀티스레드에서 안전한(Atomic) 래퍼 클래스를 사용합니다.
    // 설명: 내부적으로 CPU의 CAS(Compare-And-Swap) 명령어를 사용하여 락 없이 동기화합니다.
    // 왜 사용함?: synchronized보다 훨씬 빠르고 가볍습니다. (스레드가 멈추지 않음)
    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                // [문제 2] 값을 1 증가시키고 그 결과를 반환하는 메서드는?
                // 설명: (count++)와 같지만 원자적으로 실행됩니다.
                // 락을 걸지 않지만, 충돌이 나면 내부적으로 계속 재시도(Spin Lock)합니다.
                count.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // 2000이 보장됨
        System.out.println("결과: " + count.get());
    }
}