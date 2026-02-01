package com.example.playground.lang.문자열;

import java.util.Arrays;
import java.util.function.Predicate;

// 문자열 연산의 효율성을 테스트하는 문제입니다.

public class StringTestMain {
    public static void main(String[] args) {
        String[] words = {"Java", "Spring", "JPA", "Querydsl"};

        // TODO: 1. StringBuilder를 사용하여 배열 안의 단어들을 연결하세요.
        // 연결 결과 예시: "JavaSpringJPAQuerydsl"
        StringBuilder sb = new StringBuilder();
        for(String word : words){
            sb.append(word);
        }


        String result = sb.toString();
        System.out.println("Result: " + result);

        // TODO: 2. 다음 문장에서 "apple"이 몇 번 나오는지 계산하는 로직을 작성하세요.
        String str = "apple banana apple orange apple";
        // 정답 코드를 작성하세요. (split 혹은 indexOf 등을 활용)
        String[] splitWords = str.split(" ");

        long appleCount = Arrays.stream(splitWords).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.contains("apple");
            }
        }).count();

        //TODO: 3. indexOf 방식
        /*
        // TODO 3: indexOf 방식 정답
        String str = "apple banana apple orange apple";
        int count = 0;
        int index = str.indexOf("apple"); // 1. 먼저 첫 번째 apple을 찾습니다.

        while (index != -1) { // 2. 더 이상 찾을 apple이 없을 때까지(-1이 아닐 때까지) 반복합니다.
            count++;
            // 3. 찾은 위치 다음 인덱스부터 다시 검색을 시작합니다. ( indexOf(str, fromIndex) )
            index = str.indexOf("apple", index + "apple".length());
        }

        System.out.println("indexOf count: " + count);
         */

        // (*참고*) 람다 버전
        // long appleCount = Arrays.stream(splitWords).filter(s -> s.contains("apple")).count();

        System.out.println(appleCount);

    }
}