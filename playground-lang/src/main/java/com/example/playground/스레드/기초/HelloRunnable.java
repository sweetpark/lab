package com.example.playground.스레드.기초;

public class HelloRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Hello");
    }

    public static void main(String[] args) {
        System.out.println("main 스레드 시작");

        // 작업 내용(Runnable) 인스턴스 생성
        HelloRunnable task = new HelloRunnable();

        // [문제 3] 작업을 수행할 실제 '스레드(일꾼)' 객체 생성
        // 설명: task(작업)만으로는 실행되지 않습니다. 이것을 수행할 주체(Thread)에게 넘겨줘야 합니다.
        // 왜 중요한가?: 작업(Runnable)과 실행기(Thread)를 분리해야 유연한 설계가 가능하기 때문입니다.
        Thread thread = new Thread(task);

        // [문제 4] 스레드를 시작시키는 메서드는?
        // 중요: thread.run()을 호출하면 안 됩니다!
        // 왜 중요한가?: run()을 직접 호출하면, 새로운 스레드가 생성되지 않고 단순히 main 스레드가 메서드를 호출하는 꼴이 됩니다.
        // 이 메서드를 호출해야만 OS에게 "새로운 스택 공간을 할당해줘"라고 요청하여 멀티 스레딩이 시작됩니다.
        thread.start();

        System.out.println("main 스레드 종료");
    }
}
