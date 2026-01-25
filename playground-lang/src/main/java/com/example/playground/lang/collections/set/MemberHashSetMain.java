package com.example.playground.lang.collections.set;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
 * [주제: HashSet과 사용자 정의 객체]
 * * 1. 이 주제를 연습하는 이유:
 * - HashSet이 내부적으로 hashCode()와 equals()를 어떻게 사용하여 중복을 제거하는지 이해하기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 이 메서드들을 구현하지 않으면, 데이터 내용이 같아도 중복으로 저장되어 Set의 기능을 잃게 됩니다.
 */

class Member {
    private String id;

    public Member(String id) {
        this.id = id;
    }

    // TODO 1: id가 같으면 같은 해시코드가 나오도록 hashCode()를 오버라이딩하세요.
    // HASH는 HashMap, HashSet에서 사용됨
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // TODO 2: id가 같으면 true를 반환하도록 equals()를 오버라이딩하세요.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public String toString() {
        return "Member{id='" + id + "'}";
    }
}

public class MemberHashSetMain {
    public static void main(String[] args) {
        Set<Member> set = new HashSet<>();

        Member m1 = new Member("idA");
        Member m2 = new Member("idA"); // m1과 id가 같음

        set.add(m1);
        set.add(m2);

        // TODO 3: set의 크기가 1인지 확인하고 출력하세요.
        System.out.println("Set 크기: " + set.size());
        System.out.println("결과: " + set);
    }
}