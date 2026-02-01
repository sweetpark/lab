package com.example.playground.람다;

import java.util.function.*;

public class 메서드참조 {
    public static void main(String[] args) {

        // 1. 스태틱 메서드 참조 (Static Method)
        // 람다: (str) -> Integer.parseInt(str)
        // 설명: "Integer 클래스의 parseInt 스태틱 메서드를 쓸 거야"
        Function<String, Integer> stringToInt = Integer::parseInt;

        System.out.println("스태틱 참조: " + stringToInt.apply("100"));


        // 2. 인스턴스 메서드 참조 (Instance Method)
        // 람다: (msg) -> System.out.println(msg)
        // 설명: "System.out 이라는 인스턴스가 가진 println 메서드를 쓸 거야"
        // 왜 사용함?: forEach 문에서 가장 많이 보이는 패턴입니다.
        Consumer<String> printer = System.out::println;

        printer.accept("인스턴스 참조 출력");


        // 3. 임의 객체의 인스턴스 메서드 참조 (Arbitrary Object)
        // 람다: (str) -> str.length()
        // 설명: "들어오는 String 객체(str)의 length 메서드를 쓸 거야"
        // 주의: 위 2번과 다르게, '누구의' 메서드인지가 파라미터로 넘어옵니다.
        Function<String, Integer> strLength = String::length;

        System.out.println("길이 참조: " + strLength.apply("Hello"));


        // 4. 생성자 참조 (Constructor)
        // 람다: () -> new Member()
        // 설명: "Member 클래스의 생성자(new)를 호출할 거야"
        // 왜 사용함?: 객체를 공장에서 찍어내듯 생성할 때 씁니다. (Factory 패턴 대체)
        Supplier<Member> factory = Member::new;

        Member member = factory.get();
        System.out.println("생성자 참조: " + member);
    }
}

class Member {
    @Override
    public String toString() {
        return "새로운 멤버 생성됨";
    }
}