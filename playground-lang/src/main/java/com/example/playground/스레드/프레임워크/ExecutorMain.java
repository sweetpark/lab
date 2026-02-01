package com.example.playground.스레드.프레임워크;

import java.util.concurrent.*;

public class ExecutorMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // [문제 1] 스레드 풀을 관리하는 인터페이스입니다.
        // 구현체로는 ThreadPoolExecutor가 있지만, 보통 Executors 팩토리 메서드로 만듭니다.
        // 설명: 스레드 5개를 미리 만들어놓고 재사용하는 풀(Pool)을 생성합니다.
        // 왜 사용함?: 스레드 생성 비용을 아끼고, 스레드 개수를 제한하여 서버 폭주를 막습니다.
        ExecutorService es = Executors.newFixedThreadPool(5);

        // [문제 2] Runnable과 비슷하지만, '리턴값'이 있는 작업을 정의하는 인터페이스는?
        // 설명: "전화를 걸 수 있는(Call...)" 뜻의 단어입니다. 제네릭으로 반환 타입을 지정합니다.
        // 왜 사용함?: 스레드 작업이 끝나고 계산 결과(여기선 Integer)를 main 스레드로 가져와야 할 때 씁니다.
        Callable<Integer> task = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(3000);
                return 100 + 200; // 결과 반환 가능!
            }
        };

        // [문제 3] 작업을 스레드 풀에 제출(submit)하면, 결과를 담을 '바구니'를 즉시 돌려줍니다.
        // 설명: "미래(Future)"의 값을 담는 객체입니다.
        // 중요: es.submit()은 스레드가 작업을 다 할 때까지 멈추는 게 아니라, 일단 이 객체만 던져주고 바로 다음 줄로 넘어갑니다.
        Future<Integer> future = es.submit(task);

        System.out.println("작업 제출 완료. 메인 스레드는 다른 일 하는 중...");

        // 나중에 결과가 필요할 때 꺼내 씁니다.
        // 이때 작업이 아직 안 끝났다면, 끝날 때까지 여기서 기다립니다(Blocking). >> .get()이 블로킹
        Integer result = future.get();

        System.out.println("작업 결과: " + result);

        // 스레드 풀 종료 (안 하면 프로그램이 안 꺼짐)
        es.shutdown();
    }
}