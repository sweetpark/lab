package com.example.playground.스트림;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamBasicMain {
    public static void main(String[] args) {
        List<String> names = List.of("kim", "park", "lee", "choi", "kim");

        // [문제 1] 컬렉션(List)에서 스트림을 생성하는 메서드는?
        // 설명: 수도꼭지(Source)를 트는 것과 같습니다. 데이터의 흐름을 시작합니다.
        Stream<String> stream = names.stream();

        Stream<String> resultStream = stream
                // [문제 2] 데이터를 걸러내는 중간 연산 (조건: 이름에 'k'가 포함된 것)
                // 설명: Predicate(조건) 람다를 사용합니다.
                .filter(name -> name.contains("k"))

                // [문제 3] 데이터를 변환하는 중간 연산 (이름 -> 대문자)
                // 설명: Function(변환) 람다를 사용합니다.
                .map(String::toUpperCase);

        // 중요: 여기까지 코드를 실행하면 화면에 아무것도 출력되지 않고, 연산도 수행되지 않습니다.
        // 왜냐하면 스트림은 '최종 연산'이 오기 전까지는 실행을 미루기 때문입니다.
        // 이것을 "______ 연산" (Lazy Evaluation)이라고 합니다.

        // [문제 4] 실제 연산을 시작하고 결과를 리스트로 모으는 '최종 연산'은?
        // (Java 16부터 지원하는 간편 메서드)
        List<String> result = resultStream.toList();

        System.out.println(result); // [KIM, PARK, KIM]
    }
}