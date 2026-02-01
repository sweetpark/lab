package com.example.playground.lang.자료구조.Deque;

import java.util.ArrayDeque;
import java.util.Deque;

/*
 * [주제: Stack(LIFO)과 Queue(FIFO)]
 * * 1. Stack: 후입선출 (나중에 들어온 게 먼저 나감) - 예: 뒤로가기, 실행 취소
 * * 2. Queue: 선입선출 (먼저 들어온 게 먼저 나감) - 예: 줄 서기, 인쇄 대기열
 */

public class StackQueueMain {
    public static void main(String[] args) {
        // 자바의 Stack 클래스는 성능상 권장되지 않으므로 ArrayDeque를 사용합니다.
        Deque<Integer> stack = new ArrayDeque<>();
        Deque<Integer> queue = new ArrayDeque<>();


        System.out.println("===== STACK =====");
        // TODO 1: Stack에 1, 2, 3을 넣고(push), 꺼내면서(pop) 출력하세요.
        // 출력 예상: 3, 2, 1
        stack.push(1);
        stack.push(2);
        stack.push(3);

        while(!stack.isEmpty()){
            System.out.printf(stack.pop() + " ");
        }

        System.out.println("\n====== QUEUE =====");



        // TODO 2: Queue에 1, 2, 3을 넣고(offer), 꺼내면서(poll) 출력하세요.
        // 출력 예상: 1, 2, 3
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);

        while(!queue.isEmpty()){
            System.out.printf(queue.poll() + " ");
        }

    }
}