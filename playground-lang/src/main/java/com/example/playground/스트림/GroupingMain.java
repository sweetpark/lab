package com.example.playground.스트림;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupingMain {
    public static void main(String[] args) {
        List<Student> students = List.of(
                new Student("kim", 10, "Seoul"),
                new Student("park", 20, "Seoul"),
                new Student("lee", 10, "Busan")
        );

        // 목표: 지역(City)별로 학생들을 그룹핑해서 Map에 담기
        // 결과 예시: { "Seoul": [kim, park], "Busan": [lee] }

        Map<String, List<Student>> map = students.stream()
                // [문제 1] 스트림의 데이터를 모으는 최종 연산의 표준 메서드는?
                // 설명: 파라미터로 Collector 구현체를 받습니다.
                .collect(
                        // [문제 2] 특정 기준(여기선 city)으로 데이터를 그룹핑해주는 Collector는?
                        // 설명: SQL의 GROUP BY와 같습니다.
                        Collectors.groupingBy(Student::getCity)
                );

        System.out.println(map);
    }

    // (Student 클래스는 getter가 있다고 가정)
    static class Student {
        String name; int age; String city;
        public Student(String name, int age, String city) {
            this.name = name; this.age = age; this.city = city;
        }
        public String getCity() { return city; }
        public String toString() { return name; }
    }
}