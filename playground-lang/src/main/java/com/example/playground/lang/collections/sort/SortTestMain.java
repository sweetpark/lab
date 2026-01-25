package com.example.playground.lang.collections.sort;

import java.util.*;

/*
 * [주제: 객체의 정렬]
 * 1. Comparable: 객체 스스로가 가지는 기본 정렬 기준을 구현 (예: 학생은 점수순이 기본)
 * 2. Comparator: 기본 기준 외에 특별한 정렬 기준이 필요할 때 사용 (예: 이름순 정렬)
 */

class Student implements Comparable<Student> {
    String name;
    int score;

    Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    // TODO 1: 기본 정렬 기준을 '점수 내림차순'으로 설정하세요.
    @Override
    public int compareTo(Student other) {
        // 내 점수가 상대보다 크면 음수, 작으면 양수를 반환하면 내림차순이 됩니다.
        // return Integer.compare(other.score, this.score);
        return this.score - other.score; // 완성해주세요.
    }

    @Override
    public String toString() {
        return name + "(" + score + ")";
    }
}

public class SortTestMain {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("학생A", 80));
        students.add(new Student("학생C", 100));
        students.add(new Student("학생B", 90));

        System.out.println("== 기본 정렬 (점수 내림차순) ==");
        Collections.sort(students);
        System.out.println(students);

        // TODO 2: Comparator를 사용하여 '이름 오름차순'으로 정렬하세요.
        System.out.println("== 이름 오름차순 정렬 ==");
        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o2.compareTo(o1); // 완성해주세요.
            }
        });
        System.out.println(students);
    }
}