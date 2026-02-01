package com.example.playground.스레드.기초;

public class SynchronizedMain {
    private int balance;

    public SynchronizedMain(int initialBalance) {
        this.balance = initialBalance;
    }

    // [문제 1] 여러 스레드가 동시에 이 메서드를 호출해도,
    // 한 번에 하나의 스레드만 진입할 수 있게 막아주는 키워드는?
    // 설명: 이 키워드가 붙으면 해당 인스턴스(this)의 락(Lock)을 획득해야만 코드를 실행할 수 있습니다.
    // 왜 중요한가?: '잔액 확인'과 '출금'이라는 두 단계의 작업이 끊기지 않고 하나의 원자적(Atomic) 작업처럼 실행됨을 보장합니다.
    public synchronized boolean withdraw(int amount) {

        System.out.println("거래 시작: " + Thread.currentThread().getName());

        // 1. 잔액 확인 (검증 단계)
        if (balance < amount) {
            return false;
        }

        // 출금 진행 (시간이 조금 걸린다고 가정하여 동시성 문제 유발)
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // 2. 잔액 차감 (실행 단계)
        balance = balance - amount;

        System.out.println("거래 종료: " + Thread.currentThread().getName());
        return true;
    }

    // [문제 2] 잔액을 조회하는 메서드에도 안전장치가 필요할까요?
    // 설명: 조회만 하더라도 동기화를 하는 것이 좋습니다.
    // 왜 중요한가?: 다른 스레드가 출금하는 도중에(락을 걸고 작업 중일 때)
    // 엉뚱한 중간값(계산 중인 값)을 읽어가는 것을 방지하고, '메모리 가시성'도 함께 확보하기 위함입니다.
    public synchronized int getBalance() {
        return balance;
    }
}
