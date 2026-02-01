package com.example.playground.람다;

public class LambdaMain {

    // [문제 1] 람다식으로 사용할 인터페이스에는 '이 애노테이션'을 붙이는 것이 좋습니다.
    // 설명: 추상 메서드가 딱 1개만 있는지 컴파일러가 검사해줍니다.
    // 왜 사용함?: 람다는 무조건 '1개의 추상 메서드'랑만 짝을 이룰 수 있기 때문에,
    // 누군가 실수로 메서드를 추가하는 것을 방지하는 안전장치입니다.
    @FunctionalInterface
    public interface Calculator {
        int calculate(int a, int b);
    }

    public static void main(String[] args) {

        // 1. (옛날 방식) 익명 클래스 사용
        Calculator anonCalc = new Calculator() {
            @Override
            public int calculate(int a, int b) {
                return a + b;
            }
        };
        System.out.println("익명 클래스 결과: " + anonCalc.calculate(10, 20));

        // ---------------------------------------------------------

        // 2. (최신 방식) 람다 표현식 사용
        // [문제 2] 위 익명 클래스를 람다식으로 바꿔보세요. (타입, return, 중괄호 생략 가능)
        // 설명: 메서드의 이름도 없고, 반환 타입도 없습니다. 오직 (매개변수)와 -> {본문} 만 남습니다.
        // 왜 중요한가?: 코드가 간결해져서 로직(a+b)이 한눈에 들어옵니다.
        // 이것이 바로 함수형 프로그래밍의 핵심인 "동작 파라미터화(Behavior Parameterization)"의 기초입니다.
        Calculator lambdaCalc = (a, b) -> a + b;

        System.out.println("람다 결과: " + lambdaCalc.calculate(10, 20));

        // ---------------------------------------------------------

        // [심화 문제 3] 람다와 캡처링 (Capturing)
        int localValue = 100;

        Calculator captureCalc = (a, b) -> {
            // [질문] 여기서 main 메서드의 지역변수 localValue에 값을 더하거나 수정할 수 있을까요?
            // localValue = localValue + 1; // <--- 이 코드는 컴파일 에러가 납니다.

            // 이유: 람다는 지역 변수를 복사해서 가져오기 때문에, 해당 변수는 사실상 final 상태여야 합니다.
            // (힌트: 'f...'로 시작하는 단어, 값이 변하지 않음)
            return a + b + localValue;
        };
    }
}