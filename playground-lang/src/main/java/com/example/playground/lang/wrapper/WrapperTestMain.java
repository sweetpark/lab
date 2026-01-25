package com.example.playground.lang.wrapper;

public class WrapperTestMain {
    // (문제 기준)
    // 기본형(int)은 객체가 아니기 때문에 null을 가질 수 없고 메서드도 없지만, 래퍼 클래스(Integer)는 객체로서의 기능을 가집니다.

    public static void main(String[] args) {
        String str1 = "100";
        String str2 = "200";

        // TODO: 1. 두 문자열을 숫자로 변환하여 합계를 구하세요. (parseInt 활용)
        System.out.println("[TODO 1] sum = " + (Integer.parseInt(str1) + Integer.parseInt(str2)));


        // TODO: 2. 오토 박싱(Auto-boxing)과 오토 언박싱(Auto-unboxing) 예제를 완성하세요.
        int primitive = 10;
        Integer wrapper = null; // 2-1. primitive 변수를 wrapper에 대입하세요 (박싱)
        wrapper = Integer.valueOf(primitive);

        int unboxed = 0; // 2-2. wrapper 변수를 다시 unboxed에 대입하세요 (언박싱)
        unboxed = wrapper.intValue();

        // TODO: 3. 다음 문자열들을 숫자로 변환하되, 하나라도 숫자가 아니면 0을 반환하는 메서드를 완성하세요.
        String[] values = {"10", "20", "five", "30"};
        int total = sumValidValues(values);
        System.out.println("[TODO 3] total = " + total); // 예상 결과: 60
    }

    private static int sumValidValues(String[] values) {
        int sum = 0;
        for (String value : values) {
            // 여기에 try-catch 혹은 정규식을 활용하여 숫자인 경우만 더하는 로직을 작성하세요.
            try{
                sum += Integer.parseInt(value);
            }catch(Exception e){
            }
        }
        return sum;
    }
}