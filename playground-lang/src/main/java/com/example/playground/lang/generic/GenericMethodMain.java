package com.example.playground.lang.generic;

/*
 * [주제: 제네릭 메서드 & 와일드카드]
 * * 1. 이 주제를 연습하는 이유:
 * - 특정 메서드에서만 타입을 유동적으로 받고 싶을 때, 혹은 특정 타입의 자식들만 허용하고 싶을 때 사용합니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - <T extends Animal> 처럼 상한 제한을 두어, 해당 타입이 가진 메서드(예: animal.sound())를 안전하게 호출할 수 있습니다.
 * * 3. 어떤 경우에 사용되는가?:
 * - 동물 병원에서 '동물'의 하위 타입만 치료하거나, 숫자 리스트에서 합계를 구하는 등 '타입 범위'를 제한할 때 사용합니다.
 */

class Unit {
    public void move() { System.out.println("유닛이 이동합니다."); }
}
class Marine extends Unit { @Override public void move() { System.out.println("마린이 이동합니다."); } }
class Zealot extends Unit { @Override public void move() { System.out.println("질럿이 이동합니다."); } }

public class GenericMethodMain {
    public static void main(String[] args) {
        Marine marine = new Marine();
        Zealot zealot = new Zealot();

        printUnit(marine);
        printUnit(zealot);
//        printUnit("Test"); 실패 코드

        // sum(String, int) 실패코드
//        double test = sum("Test", 100);
        System.out.println(sum((int)10, (int) 100)); // 이거 실패야하는거아닌가? >> Compiler가 자동으로 오토박싱 진행
        System.out.println(sum(Integer.valueOf(10), Integer.valueOf(1000)));
    }

    // TODO: Unit과 그 자식들만 매개변수로 받을 수 있는 제네릭 메서드 printUnit을 만드세요.
    // 힌트: <T extends Unit> 을 활용하세요.
    public static <T extends Unit> void printUnit(T unit) {
        unit.move();
    }

    // TODO 1: 숫자(Number)와 그 자식들만 처리할 수 있는 제네릭 메서드 sum을 만드세요.
    // 힌트: <T extends Number> 를 사용하고, 반환 타입은 double로 고정하세요.
    public static <T extends Number> double sum(T t1, T t2) {
        // Number 클래스가 가진 doubleValue()를 사용해 보세요.
        // Number extends로 intValue() 사용 가능
        return t1.intValue() + t2.intValue(); // 완성해 주세요.
    }
}




