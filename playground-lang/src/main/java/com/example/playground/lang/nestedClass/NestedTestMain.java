package com.example.playground.lang.nestedClass;

/*
 * [주제: 중첩 클래스 (Nested Class) & 익명 클래스]
 * * 1. 이 주제를 연습하는 이유:
 * - 특정 클래스가 오직 '하나의 바깥 클래스' 내부에서만 쓰인다면, 내부에 숨겨서 구조를 깔끔하게 만들기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 캡슐화 강화: 바깥 클래스의 private 멤버에 접근 가능하면서 외부에는 노출하지 않습니다.
 * - 코드 응집도: 관련 있는 코드를 물리적으로 가까운 곳에 둡니다.
 * * 3. 어떤 경우에 사용되는가?:
 * - 이벤트 처리(버튼 클릭), 데이터 정렬(Comparator 구현), 특정 클래스 전용 헬퍼 객체 등.
 */

// Runnable (스레드)와 동일 구조
interface Hello {
    void run();
}

public class NestedTestMain {
    public static void main(String[] args) {

        // TODO 1: Hello 인터페이스를 '익명 클래스'로 구현하여 "Hello Anonymous!"를 출력하세요.
        // 익명 클래스는 'new 인터페이스명() { ... }' 형태로 만듭니다.
        Hello anonymousHello = new Hello() {
            @Override
            public void run() {
                System.out.println("Hello Anonymous!");
            }
        };

        // TODO 2: 위 익명 클래스 로직을 '람다(Lambda)'로 변환하여 "Hello Lambda!"를 출력하세요.
        // 힌트: ( ) -> { ... }
        Hello lambdaHello = () -> System.out.println("Hello Lambda!");

        // 실행
        if(anonymousHello != null) anonymousHello.run();
        if(lambdaHello != null) lambdaHello.run();
    }
}