package com.example.playground.람다;

import java.util.function.*;

public class StandardInterfaceMain {
    public static void main(String[] args) {

        // 1. 공급자 (Supplier)
        // 특징: 매개변수 없이 값을 반환(Return)만 합니다.
        // [문제 1] T 타입의 값을 '제공'하는 인터페이스 이름과 메서드는?
        // 사용처: 게으른 연산(Lazy Evaluation), 임의의 값 생성 등
        Supplier<String> supplier = () -> "공급할 데이터";
        System.out.println("Supplier 결과: " + supplier.get());


        // 2. 소비자 (Consumer)
        // 특징: 매개변수를 받아서 사용(소비)하고, 리턴이 없습니다(void).
        // [문제 2] T 타입을 받아서 '수락'하고 처리하는 메서드는?
        // 사용처: 출력(System.out.println), 데이터 저장, 로그 남기기 등
        Consumer<String> consumer = (msg) -> System.out.println("소비한 메시지: " + msg);
        consumer.accept( "Hello" );


        // 3. 함수 (Function)
        // 특징: 매개변수(T)를 받아서 결과(R)를 반환합니다. (매핑)
        // [문제 3] 값을 받아서 변환 후 '적용'하는 메서드는?
        // 사용처: String을 Integer로 변환, 객체에서 특정 필드 꺼내기 등 (가장 많이 씀)
        Function<Integer, String> converter = (num) -> "숫자 변환: " + num;
        System.out.println("Function 결과: " + converter.apply( 100 ));


        // 4. 조건 (Predicate)
        // 특징: 매개변수(T)를 받아서 참/거짓(boolean)을 반환합니다.
        // [문제 4] 조건을 '테스트'하는 메서드는?
        // 사용처: filter(짝수만 걸러내기), 조건문 대체 등
        Predicate<Integer> isEven = (num) -> num % 2 == 0;
        System.out.println("Predicate 결과(짝수니?): " + isEven.test( 10 ));
    }
}