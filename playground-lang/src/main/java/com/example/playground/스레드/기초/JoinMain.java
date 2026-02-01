package com.example.playground.스레드.기초;

public class JoinMain {
    public static class SumTask implements  Runnable{
        public int result = 0;
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                result = 100;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        SumTask task = new SumTask();

        Thread thread = new Thread(task);
        thread.start();

        // [상황] thread가 계산을 마치려면 2초가 걸리는데,
        // main 스레드는 여기서 바로 다음 줄로 넘어가려고 합니다.

        try {
            // [문제 1] thread의 작업이 끝날 때까지 main 스레드를 기다리게 하는 메서드는?
            // 설명: 이 메서드를 호출하면 main 스레드는 해당 thread가 종료(Terminated)될 때까지 대기 상태(WAITING)가 됩니다.
            // 왜 중요한가?: 비동기로 실행되는 작업의 순서를 맞추거나, 결과값이 나올 때까지 기다려야 할 때 필수적입니다.
            // 만약 이걸 안 쓰면 계산 전의 값(0)이 출력되어 버그가 발생합니다.
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if ( task.result == 100 ){
            System.out.println("success");
        }else{
            System.out.println("fail");
        }
    }
}


