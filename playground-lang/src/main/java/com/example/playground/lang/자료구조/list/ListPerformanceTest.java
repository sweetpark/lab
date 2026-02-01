package com.example.playground.lang.자료구조.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 * [주제: List 인터페이스와 성능 비교]
 * * 1. 이 주제를 연습하는 이유:
 * - ArrayList와 LinkedList의 성능 특성을 이해하고 상황에 맞는 선택을 하기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 조회 위주라면 ArrayList, 앞부분의 빈번한 삽입/삭제라면 LinkedList가 유리합니다.
 * * 3. 어떤 경우에 사용되는가?:
 * - 실무에서는 90% 이상 ArrayList를 사용하지만, 대용량 데이터의 앞단 처리가 필요할 땐 LinkedList를 고려합니다.
 */

public class ListPerformanceTest {
    public static void main(String[] args) {
        int size = 50000; // 데이터 개수

        System.out.println("== ArrayList 추가 성능 ==");
        addLast(new ArrayList<>(), size); // 뒤에 추가
        addFirst(new ArrayList<>(), size); // 앞에 추가 (느림)

        System.out.println("\n== LinkedList 추가 성능 ==");
        addLast(new LinkedList<>(), size); // 뒤에 추가
        addFirst(new LinkedList<>(), size); // 앞에 추가 (빠름)
    }

    // TODO 1: 리스트의 맨 앞에 데이터를 추가하는 시간을 측정하는 메서드를 완성하세요.
    private static void addFirst(List<Integer> list, int size) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            // 여기에 작성: list의 0번 인덱스에 i를 추가하세요.
            list.add(0, i);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("앞에 추가 - 크기: " + size + ", 계산 시간: " + (endTime - startTime) + "ms");
    }

    // TODO 2: 리스트의 맨 뒤에 데이터를 추가하는 시간을 측정하는 메서드를 완성하세요.
    private static void addLast(List<Integer> list, int size) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            // 여기에 작성: list에 i를 순차적으로 추가하세요.
            list.add(i);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("뒤에 추가 - 크기: " + size + ", 계산 시간: " + (endTime - startTime) + "ms");
    }
}