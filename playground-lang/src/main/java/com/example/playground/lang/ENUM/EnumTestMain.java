package com.example.playground.lang.ENUM;


/*
    단순한 문자열이나 숫자로 상태를 관리하면 오타나 잘못된 값이 들어올 위험이 있습니다. Enum을 사용해 타입 안전성을 확보해 보세요.
 */

// TODO: 1. 등급(Grade)에 따른 할인율을 관리하는 ENUM을 만드세요.
// BASIC(10%), GOLD(20%), DIAMOND(30%)
enum Grade {
    BASIC("BASIC", 10), GOLD("GOLD", 20), DIAMOND("DIAMOND", 30);

    public final String name;
    public final int value;

    Grade(String name, int value){
        this.name = name;
        this.value = value;
    }
}

public class EnumTestMain {
    public static void main(String[] args) {
        int price = 10000;

        // TODO: 2. 각 등급별 할인 금액을 계산하여 출력하세요.
        // 공식: 할인 금액 = price * (할인율 / 100)
        Grade[] grades = Grade.values();
        for (Grade grade : grades) {
            printDiscount(grade, price);
        }
    }

    private static void printDiscount(Grade grade, int price) {
        // TODO: 3. enum에 정의된 할인율을 가져와서 계산하는 로직을 작성하세요.
        System.out.println("[TODO 3] "+ grade.name + " 등급의 할인 금액: " + price * grade.value / 100);
    }
}