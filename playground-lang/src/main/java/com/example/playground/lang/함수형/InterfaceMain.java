package com.example.playground.lang.함수형;

public class InterfaceMain {

    public interface Animal{


        // 추상 메서드 (구현 강제)
        void sound();

        // [문제 1] 인터페이스에 새로운 기능을 추가하고 싶은데,
        // 이미 이걸 구현한 클래스(Dog, Cat...)가 수백 개라서 다 고칠 수가 없습니다.
        // 이때 '기본 구현'을 제공하여 하위 호환성을 지켜주는 키워드는?
        // 설명: Java 8부터 인터페이스도 몸통({ body })을 가진 메서드를 가질 수 있게 되었습니다.
        default void breath() {System.out.println("호흡");}
    }

    static class Dog implements Animal{
        @Override
        public void sound() {
            System.out.println("멍 ~ 멍 ~");
        }
        // breathe()를 오버라이딩 안 해도 에러가 안 남! (기본 구현 사용)
    }

    public static void main(String[] args) {
        Animal dog = new Dog();

        dog.sound();
        dog.breath();// "호흡" 출력
    }
}
