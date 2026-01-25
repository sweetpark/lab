package com.example.playground.lang.immutable;

/**
 * 사용자 주소 정보를 담는 클래스입니다.
 * 이 클래스를 '불변 객체'로 만드세요.
 */

// 이 문제는 객체의 상태가 외부에서 변하지 않도록 설계하고, 값을 변경하고 싶을 때 새로운 객체를 생성하는 원리를 익히는 문제입니다.
public class ImmutableAddress {

    // TODO: 1. 필드를 수정할 수 없도록 키워드를 추가하세요.
    private final String value;

    public ImmutableAddress(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // TODO: 2. 주소를 변경하려는 시도가 있을 때,
    // 기존 객체의 값을 바꾸지 않고 새로운 객체를 생성하여 반환하는 메서드를 완성하세요.
    // 새로운 객체 생성 ( 불변객체를 사용하는 목적은 변경에 대한 안전 >> 공유 자원에 대한 해결 가능 ( 멤버 필드 값 )
    public ImmutableAddress withValue(String newValue) {
        return new ImmutableAddress(newValue); // 정답을 적어주세요.
    }

    @Override
    public String toString() {
        return "Address{" + "value='" + value + '\'' + '}';
    }
}