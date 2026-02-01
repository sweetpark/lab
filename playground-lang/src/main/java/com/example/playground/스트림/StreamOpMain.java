package com.example.playground.스트림;

import java.util.List;

public class StreamOpMain {
    public static void main(String[] args) {
        List<String> numbers = List.of("1", "2", "3");

        // 상황: 문자열 리스트를 숫자로 바꿔서 합계를 구하고 싶음.

        int sum = numbers.stream()
                // [문제 1] 일반 map을 쓰면 Stream<Integer>가 되어 비효율적입니다.
                // int형 전용 스트림(IntStream)으로 변환해주는 메서드는?
                // 설명: 성능 최적화를 위해 숫자 연산 시 필수입니다.
                .mapToInt(Integer::parseInt)

                // [문제 2] IntStream이 제공하는 편리한 총합 계산 메서드는?
                // 설명: reduce((a,b) -> a+b)를 더 쉽게 쓴 것입니다.
                .sum();

        System.out.println("합계: " + sum);


        // [심화] 2차원 배열 납작하게 만들기
        List<List<String>> nested = List.of(List.of("A"), List.of("B", "C"));

        nested.stream()
                // [문제 3] 리스트 안의 리스트를 다 꺼내서 '하나의 평평한 스트림'으로 만드는 메서드는?
                // 설명: 1:1 변환은 map, 1:N 변환(납작하게)은 이 메서드를 씁니다.
                // 결과: [ ["A"], ["B","C"] ] -> [ "A", "B", "C" ]
                .flatMap(List::stream)
                .forEach(System.out::println);
    }
}