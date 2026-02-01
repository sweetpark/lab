package com.example.playground.스레드.프레임워크;

import java.util.concurrent.*;

public class ExecutorAdvancedMain {

    public static void main(String[] args) {

        // [심화 1] 직접 ThreadPoolExecutor를 생성하여 극한의 제어를 합니다.
        // 실무에서는 Executors.newFixed... 보다 이걸 직접 써서 파라미터를 튜닝하는 경우가 많습니다.
        ExecutorService es = new ThreadPoolExecutor(
                2, // corePoolSize: 기본으로 유지할 스레드 수 (항상 대기)
                4, // maxPoolSize: 트래픽이 몰리면 최대 여기까지 스레드를 늘림
                60, TimeUnit.SECONDS, // keepAliveTime: 늘어난 스레드가 할 일 없으면 60초 뒤 제거
                new ArrayBlockingQueue<>(100), // workQueue: 대기열도 100개로 제한 (중요: 무제한이면 OOM 발생 위험)

                // [문제 1] 큐(100개)도 꽉 차고, 스레드(4개)도 다 찼을 때 들어온 요청을 처리하는 정책은?
                // 설명: "요청한 놈(Caller)이 직접 실행해라" 라는 뜻의 정책입니다.
                // 왜 중요함?: 요청을 그냥 버리면(Abort) 에러가 나고, 큐에 무한정 쌓으면 메모리가 터집니다.
                // 이걸 쓰면 요청한 스레드(main)가 직접 일을 하느라 바빠져서, 자연스럽게 요청 속도가 조절됩니다.
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 작업 제출 (Callable)
        Future<String> future = es.submit(() -> {
            Thread.sleep(2000);
            return "Task Complete";
        });

        try {
            // [문제 2] 결과가 나올 때까지 마냥 기다리면 안 됩니다. (무한 대기 방지)
            // 설명: "2초까지만 기다리고, 안 나오면 에러 던져!"
            // 왜 중요함?: 외부 API나 DB가 느려서 응답이 안 올 때, 스레드가 영원히 묶이는 것을 방지합니다.
            String result = future.get(2, TimeUnit.SECONDS);
            System.out.println("결과: " + result);

        } catch (TimeoutException e) {
            System.out.println("작업 시간이 초과되어 포기합니다.");
            // 필요 시 future.cancel(true)로 작업 취소 요청
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [문제 3] 서버를 끌 때, 진행 중인 작업은 마치고 꺼야겠죠? (우아한 종료)
        // 설명: "이제 신규 작업은 안 받겠지만, 이미 받은 건 다 처리하고 끄겠다"는 메서드.
        es.shutdown();

        try {
            // 설명: 10초 정도는 기다려주는데, 그래도 안 끝나면?
            if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("너무 안 끝나서 강제 종료합니다.");

                // 설명: 남아있는 작업들을 강제로 인터럽트 걸고 종료시킴
                es.shutdownNow();
            }
        } catch (InterruptedException e) {
            es.shutdownNow();
        }
    }
}
