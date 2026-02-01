package com.example.playground.lang.자료구조.hash;

/*
 * [주제: 해시 알고리즘과 해시 인덱스]
 * * 1. 이 주제를 연습하는 이유:
 * - 데이터의 값을 배열의 인덱스로 변환하여 검색 성능을 O(1)로 만드는 원리를 이해하기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 데이터가 1억 개여도 해시 알고리즘을 통하면 한 번에 데이터를 찾을 수 있기 때문입니다.
 *
 * 해시는 데이터 자체를 hash하여 인덱스에 넣는 구조 >> 배열의 크기가 손해볼수도 있고, 겹칠수도 있음, 검색은 O(1)
 */

public class HashIndexMain {
    public static void main(String[] args) {
        int capacity = 10; // 배열의 크기
        int[] values = {1, 2, 5, 8, 14, 99, 19};

        System.out.println("== 해시 인덱스 계산 ==");
        for (int value : values) {
            // TODO 1: value를 capacity로 나눈 나머지(해시 인덱스)를 구하세요.
            int hashIndex = hashIndex(value, capacity);
            System.out.println("값: " + value + " -> 해시 인덱스: " + hashIndex);
        }
    }

    // TODO 2: 해시 인덱스를 구하는 메서드를 완성하세요. (값 % 크기)
    static int hashIndex(int value, int capacity) {
        return value % capacity;
    }
}