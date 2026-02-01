package com.example.playground.스레드.기초;

import java.util.ArrayDeque;
import java.util.Queue;

public class WaitNotifyMain {

    public static class BoundedQueue{
        private final Queue<String> queue = new ArrayDeque<>();
        private final int max;

        public BoundedQueue(int max) {
            this.max = max;
        }

        public synchronized void put(String data) {
            // 버퍼가 가득 찼다면 생산자는 기다려야 합니다.
            while (queue.size() == max) {
                try {
                    // [문제 1] 현재 잡고 있는 락(Lock)을 풀고,
                    // 누군가 깨워줄 때까지 대기실(Wait Set)로 가서 잡니다.
                    // 설명: Object 클래스의 메서드입니다.
                    // 왜 중요한가?: 의미 없는 무한 루프(Busy Waiting)를 방지하여 CPU 자원을 아낍니다.
                    System.out.println("[생산자] 큐가 꽉 참 -> 대기");
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            queue.offer(data);
            System.out.println("생산 완료");

            // [문제 2] 대기실에서 자고 있는 스레드들에게 "일어나!"라고 신호를 보냅니다.
            // 설명: wait() 하고 있는 스레드를 깨웁니다. (보통 모든 스레드를 깨우는 메서드를 권장합니다)
            // 왜 중요한가?: 생산자가 데이터를 넣었으니, 기다리던 소비자가 깨어나서 일을 해야 하기 때문입니다.

            // [정답 2] notify()도 동작은 하지만, notifyAll()이 권장됩니다.
            // 이유: wait() 중인 '모든' 스레드를 깨워서, 혹시 모를 신호 유실이나
            // 생산자가 생산자를 깨우는 상황 등을 방어적으로 처리하기 위함입니다.
            this.notifyAll();
        }

        public synchronized String take() {
            // 버퍼가 비어있다면 소비자는 기다려야 합니다.
            while (queue.isEmpty()) {
                try {
                    // [문제 1과 동일] 데이터가 들어올 때까지 잡니다.
                    System.out.println("[소비자] 큐가 빔 -> 대기");
                    // [정답 1] 락 반납 후 대기
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            String data = queue.poll();

            // [문제 2와 동일] 빈 공간이 생겼으니, 기다리던 생산자를 깨웁니다.
            this.notifyAll(); // [정답 2] 대기 중인 생산자(혹은 다른 소비자) 모두 깨움

            return data;
        }
    }

    public static void main(String[] args) {

        BoundedQueue queue = new BoundedQueue(2);

        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++){
                    queue.put("data " + i);
                }
            }
        });

        Thread takeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    queue.take();
                }
            }
        });

        putThread.start();
        takeThread.start();
    }
}
