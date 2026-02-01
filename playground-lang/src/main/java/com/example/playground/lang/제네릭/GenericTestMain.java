package com.example.playground.lang.제네릭;

/*
 * [주제: 제네릭 (Generic)]
 * * 1. 이 주제를 연습하는 이유:
 * - 클래스 내부에서 사용할 타입을 외부에서 지정하게 함으로써, 코드 중복을 줄이고 타입 안전성을 높이기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 제네릭이 없으면 Object를 사용해야 하는데, 이 경우 잘못된 타입이 들어와도 컴파일 타임에 잡지 못하고 실행 시점에 에러(ClassCastException)가 발생합니다.
 * - 제네릭을 쓰면 '컴파일 타임'에 타입을 체크할 수 있어 안전합니다.
 * * 3. 어떤 경우에 사용되는가?:
 * - 다양한 타입을 담아야 하는 박스, 리스트, 맵 등의 자료구조나 공통 유틸리티 클래스를 만들 때 필수입니다.
 */

// TODO 1: 제네릭 클래스 Container를 만드세요.
// 이 클래스는 어떤 타입이든 하나를 보관할 수 있어야 합니다.
class Container<T> {
    protected T first;
    protected void setFirst(T first) {
        this.first = first;
    }
    protected T getFirst() {
        return this.first;
    }
    public boolean isEmpty() {
        return this.first  == null;
    }
}

/*
 1. Container를 상속하여, 불필요한 구현 생략
 2. protected의 활용
 ** 참고 **) 권장되는 방식은 서로 다른 클래스로 분리하는 것
 */
class Pair<T, F> extends Container<T>{

    private F second;
    public void setSecond(F second) { this.second = second; }
    public F getSecond() { return second; }
    public boolean isEmpty() {
        return this.first == null || this.second  == null;
    }

}

public class GenericTestMain {
    public static void main(String[] args) {
        // TODO 2: Container에 String 타입을 지정하여 "Hello"를 저장하고 출력하세요.
        Container<String> stringContainer = new Container<>();
        stringContainer.setFirst("Hello");
        System.out.println("저장된 값: " + stringContainer.getFirst());

        // TODO 3: Container에 Integer 타입을 지정하여 100을 저장하고 출력하세요.
        Container<Integer> intContainer = new Container<>();
        intContainer.setFirst(100);
        System.out.println("저장된 값: " + intContainer.getFirst());

        Container<Integer> emptyContainer = new Container<>();
        if(emptyContainer.isEmpty()){
            System.out.println("EMPTY CONTAINER!!");
        }

        // TODO 4: (심화) 두 개의 서로 다른 타입을 보관하는 Pair 클래스를 제네릭으로 만드세요.
         Pair<Integer, String> pair = new Pair<>();
         pair.setFirst(100);
         pair.setSecond("Test");

        System.out.println("저장된 값 (First) : " + pair.getFirst());
        System.out.println("저장된 값 (Second) : " + pair.getSecond());


        Pair<Integer, String> emptyPair = new Pair<>();
        emptyPair.setSecond("10");
        if(emptyPair.isEmpty()){
            System.out.println("PAIR EMPTY !!");
        }
    }
}