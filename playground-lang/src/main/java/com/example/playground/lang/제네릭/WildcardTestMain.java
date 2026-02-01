package com.example.playground.lang.제네릭;

import java.util.ArrayList;
import java.util.List;

/*
 * [주제: 와일드카드 (Wildcard)]
 * * 1. 이 주제를 연습하는 이유:
 * - 제네릭 타입을 좀 더 유연하게 매개변수로 전달받기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - Generic<Marine>은 Generic<Unit>의 자식이 아닙니다. (제네릭의 불변성)
 * - 따라서 Generic<? extends Unit>을 써야 Marine, Zealot 등을 모두 인자로 받을 수 있습니다.
 */

 class Original {
    public String name;
    public Original(String name) { this.name = name; }
}
class TestMarine extends Original { public TestMarine(String name) { super(name); } }
class TestZealot extends Original { public TestZealot(String name) { super(name); } }

public class WildcardTestMain {
    public static void main(String[] args) {
        List<TestMarine> marines = new ArrayList<>();
        marines.add(new TestMarine("마린1"));
        marines.add(new TestMarine("마린2"));

        List<TestZealot> zealots = new ArrayList<>();
        zealots.add(new TestZealot("질럿1"));

        // 호출부
        printUnits(marines);
        printUnits(zealots);
        // printUnits(List.of(new Object())); // [컴파일 오류] Original 상속 파라미터만 가능!
    }

    // TODO: 와일드카드를 사용하여 Unit과 그 하위 타입을 담은 List를 인자로 받는 메서드를 만드세요.
    // 힌트: List<? extends Unit> list
    public static void printUnits(List<? extends Original> list) {
        for (Original unit : list) {
            System.out.println("유닛 이름: " + unit.name);
        }
    }
}
