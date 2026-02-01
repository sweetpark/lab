package com.example.playground.리플렉션;

import java.lang.annotation.*;
import java.lang.reflect.Method;

public class ReflectionMain {

    // 1. 테스트용 애노테이션 정의 (런타임까지 유지되도록 설정)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Mapping {
        String value();
    }

    // 2. 테스트용 컨트롤러 클래스
    static class MyController {
        @Mapping("/hello")
        public void hello() {
            System.out.println("hello 메서드 호출됨!");
        }
    }

    public static void main(String[] args) throws Exception {
        MyController controller = new MyController();

        // 클래스 정보(거울)를 가져옵니다.
        Class<?> clazz = controller.getClass();

        // -------------------------------------------------------
        // [문제 1] 이름이 "hello"인 메서드의 정보를 찾아서 꺼내옵니다.
        // 설명: 클래스 정보(clazz)에서 특정 이름의 메서드 객체(Method)를 조회합니다.
        // 왜 사용함?: 코드를 짤 때는 어떤 메서드를 호출할지 모르고, 실행 중에 문자열(String)로 메서드를 찾아야 하기 때문입니다.
        Method method = clazz.getMethod( "hello" );
        // -------------------------------------------------------

        System.out.println("찾은 메서드: " + method.getName());

        // -------------------------------------------------------
        // [문제 2] 찾은 메서드를 실제로 실행(호출)합니다.
        // 설명: "메서드야, 이 인스턴스(controller)를 사용해서 실행해줘"라고 명령합니다.
        // 왜 사용함?: method.hello() 처럼 직접 호출할 수 없으므로(타입을 모르니까), 리플렉션으로 실행해야 합니다.
        method.invoke( controller );
        // -------------------------------------------------------


        // [심화] 애노테이션 체크하기
        // 상황: 메서드 위에 @Mapping 이라는 딱지가 붙어있는지 확인하고 싶음

        // -------------------------------------------------------
        // [문제 3] 해당 메서드에 Mapping 애노테이션이 붙어있는지 확인하는 메서드는?
        // 설명: boolean 타입을 반환합니다.
        // 왜 중요한가?: 스프링은 모든 메서드를 다 실행하는 게 아니라, @Mapping이 붙은 메서드만 골라서 URL과 연결하기 때문입니다.
        if (method.isAnnotationPresent( Mapping.class )) {

            // 애노테이션의 값("/hello")을 꺼내옵니다.
            Mapping annotation = method.getAnnotation(Mapping.class);
            System.out.println("매핑된 URL: " + annotation.value());
        }
        // -------------------------------------------------------
    }
}