package com.example.playground.lang.optional;

import java.util.Optional;

public class OptionalMain {
    public static void main(String[] args) {

        // 상황: DB에서 회원을 조회했는데, 없을 수도 있음 (null 가능성)
        String userId = "user1";
        Member member = findMember(userId);

        // [문제 1] null일 수도 있는 객체를 Optional 상자로 감싸는 메서드는?
        // 설명: 값이 있으면 상자에 담고, null이면 빈 상자(Optional.empty)를 반환합니다.
        // 왜 사용함?: Optional.of()는 null을 넣으면 터지지만, 이 메서드는 안전합니다.
        Optional<Member> optMember = Optional.ofNullable(member);

        // [문제 2] 값이 있으면 이름을 꺼내오고, 없으면 "Unknown"을 반환하는 체이닝을 완성하세요.
        String memberName = optMember
                // 설명: 상자 안의 Member 객체를 꺼내서 이름(String)으로 변환 (Stream의 map과 동일)
                .map(Member::getName)

                // 설명: 값이 비어있다면(null 상황) 대체값을 반환하는 메서드
                // 팁: orElseGet()을 쓰면 대체값 계산을 지연(Lazy)시킬 수 있어 성능에 더 좋습니다.
                .orElseGet(() -> "UNKNOWN");

        System.out.println("회원 이름: " + memberName);


        // [심화] 예외 던지기
        // 값이 없으면 "회원이 없습니다"라는 예외를 던지고 싶다면?
        // Member foundMember = optMember.______(() -> new IllegalStateException("회원이 없습니다"));
    }

    static Member findMember(String id) {
        return null; // 테스트를 위해 null 반환
    }

    static class Member {
        String name;
        public String getName() { return name; }
    }
}