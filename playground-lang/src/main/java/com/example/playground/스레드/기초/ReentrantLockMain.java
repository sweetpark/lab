package com.example.playground.스레드.기초;

import javax.swing.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockMain {
    public static class BoundedQueue{

        // [문제 1] synchronized 대신 사용할 '재진입 가능한 락' 구현체를 생성합니다.
        // 설명: 가장 일반적인 Lock 인터페이스의 구현체입니다.
        // 왜 사용함?: synchronized보다 더 정교한 제어(대기 시간 설정, 공정성 설정 등)가 가능합니다.
        private final Lock lock = new ReentrantLock();

        // [문제 2] 락에서 '스레드 대기실(Condition)'을 생성합니다.
        // 중요: 생산자와 소비자의 대기 공간을 분리하기 위해 2개를 만듭니다.
        // 왜 중요한가?: 이것이 있어야 "생산자만 깨워줘", "소비자만 깨워줘"가 가능해집니다.
        private final Condition produceCondition = lock.newCondition();
        private final Condition consumerCondition = lock.newCondition();

        private Queue<String> queue= new ArrayDeque<>();
        private final int max;

        public BoundedQueue(int max) {
            this.max = max;
        }

        public void put(String data){
                lock.lock();
            try{
                while(queue.size() == max){
                    try {
                        // [문제 3] 생산자를 '생산자 대기실'에서 기다리게 합니다. (wait 대체)
                        // 설명: Condition 인터페이스의 대기 메서드입니다.
                        this.produceCondition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.println("생산완료");
                queue.offer(data);

                // [문제 4] 데이터가 들어왔으니 '소비자 대기실'에 있는 스레드에게만 신호를 보냅니다. (notify 대체)
                // 설명: Condition 인터페이스의 알림 메서드입니다.
                // 왜 중요한가?: 불필요하게 다른 생산자를 깨우지 않고, 일을 해야 할 소비자만 정확히 깨웁니다. (효율성 UP)
                this.consumerCondition.signal();
            }catch(Exception e){
                throw new RuntimeException(e);
            }finally {
                // [문제 5] 락을 반드시 해제해야 합니다.
                // 중요: synchronized는 자동으로 해제되지만, Lock은 개발자가 직접 해제해야 합니다.
                // 만약 예외가 터져서 이 코드가 실행 안 되면, 락을 영원히 쥐고 있어 시스템이 멈춥니다(Deadlock).
                lock.unlock();
            }
        }

        public void take(){
            lock.lock();
            try{
                while(queue.isEmpty()){
                    try {
                        // [문제 3과 동일] 소비자를 '소비자 대기실'에서 기다리게 합니다.
                        this.consumerCondition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.println("소비 완료");
                queue.poll();

                // [문제 4와 동일] 빈 공간이 생겼으니 '생산자 대기실'에 있는 스레드에게만 신호를 보냅니다.
                this.produceCondition.signal();
            }catch(Exception e){
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }
        }

    }


    public static void main(String[] args) {
        BoundedQueue queue = new BoundedQueue(2);
        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    queue.put("data");
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
