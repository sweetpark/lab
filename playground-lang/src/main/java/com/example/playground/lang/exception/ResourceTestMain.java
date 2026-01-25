package com.example.playground.lang.exception;

/*
 * [주제: Try-With-Resources]
 * * 1. 이 주제를 연습하는 이유:
 * - 외부 자원(File, Network, DB Connection)을 사용한 후 반드시 close()를 호출해야 하는데, 이를 수동으로 하면 실수하기 쉽기 때문입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 코드가 훨씬 간결해집니다.
 * - 예외 발생 여부와 상관없이 자바가 자동으로 자원을 반납(close)해줍니다. (실수 방지)
 * * 3. 어떤 경우에 사용되는가?:
 * - Scanner, FileInputStream, Socket 등 AutoCloseable 인터페이스를 구현한 모든 객체.
 */

import javax.management.relation.RoleInfoNotFoundException;
import java.util.Scanner;

public class ResourceTestMain {
    public static void main(String[] args) {

        // TODO: 아래 Scanner 사용 코드를 try-with-resources 구문으로 리팩토링하세요.
        // 기존 방식은 finally에서 scanner.close()를 해야 하지만, 새로운 방식을 사용해 보세요.

        System.out.println("문자를 입력하세요 (종료: exit):");

        // 여기에 작성
        int count = 0;
        try (Scanner scanner = new Scanner(System.in)) {
            while(scanner.hasNext()){
                 count++;
                 String strLine = scanner.nextLine();
                 if(strLine.contains("exit"))
                     return;

                 System.out.println(strLine);

                 if(count == 5){
                     throw new Exception("테스트 throw 예외");
                 }
             }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}