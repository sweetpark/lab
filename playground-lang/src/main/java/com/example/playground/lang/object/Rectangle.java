package com.example.playground.lang.object;

import java.util.Objects;

// 이 문제는 객체의 물리적 주소(==)가 다르더라도, 내부의 데이터가 같다면 같은 객체로 취급(equals)하도록 만드는 연습입니다.

public class Rectangle {
    private int width;
    private int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // TODO: 1. toString()을 오버라이딩 하세요.
    // 출력 포맷: "Rectangle{width=100, height=20}"
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Rectangle{width=");
        stringBuilder.append(this.width);
        stringBuilder.append("height=");
        stringBuilder.append(this.height);
        stringBuilder.append("}");
        return stringBuilder.toString(); // 정답을 적어주세요.
    }

    // TODO: 2. equals()를 오버라이딩 하세요.
    // width와 height가 같으면 true를 반환해야 합니다.
    @Override
    public boolean equals(Object o) {
        if(o instanceof Rectangle target){
            return target.width == this.width && target.height == this.height;
        }
        return false;
    }

    // TODO: 3. hashCode()도 함께 오버라이딩 하세요. (equals를 재정의하면 필수입니다)
    @Override
    public int hashCode() {
        return 0; // 정답을 적어주세요.
    }
}