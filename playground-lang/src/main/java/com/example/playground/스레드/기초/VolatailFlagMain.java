package com.example.playground.스레드.기초;

public class VolatailFlagMain {
    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName() + " start! ");
        SumTask task = new SumTask();
        Thread thread = new Thread(task);

        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // main 스레드가 값을 변경합니다.
        // 하지만 volatile이 없으면 이 변경 내용이 메인 메모리에 즉시 반영되지 않거나,
        // 반영되더라도 task 스레드가 자기 캐시만 보고 있어서 변경 사실을 모를 수 있습니다.
        task.runFlag = false;

        System.out.println(Thread.currentThread().getName() + " end! ");
    }

    static class SumTask implements Runnable{

        // [문제 1] 캐시 메모리 사용을 막고, 항상 '메인 메모리'에서 값을 읽고 쓰도록 강제하는 키워드는?
        // 설명: "변하기 쉬운", "휘발성의"라는 뜻을 가진 키워드입니다.
        // 왜 중요한가?: 한 스레드가 수정한 값을 다른 스레드가 '즉시' 볼 수 있게(가시성) 보장합니다.
        // 만약 이걸 안 쓰면, main 스레드가 false로 바꿨음에도 이 스레드는 영원히 true로 알고 무한 루프를 돌 수 있습니다.
        public volatile boolean runFlag = true;

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " start! ");


            System.out.println("runFlag를 false로 변경 시도");


            // runFlag가 false가 되면 탈출해야 함
            while (runFlag){
                //작업
            }

            System.out.println(Thread.currentThread().getName() + " end! ");
        }
    }
}
