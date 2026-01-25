package com.example.playground.lang.collections.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * [주제: HashMap 기본 활용]
 * * 1. 이 주제를 연습하는 이유:
 * - 데이터 간의 연관 관계(이름-점수, 아이디-회원정보 등)를 저장하고 검색하기 위함입니다.
 * * 2. 이걸 왜 사용하는가?:
 * - 수만 명의 학생 중 '홍길동'의 점수를 찾을 때, 리스트처럼 다 뒤질 필요 없이 즉시 찾을 수 있습니다.
 */

public class MapTestMain {
    public static void main(String[] args) {
        Map<String, Integer> studentMap = new HashMap<>();

        // TODO 1: 학생 데이터를 추가하세요.
        studentMap.put("학생A", 90);
        studentMap.put("학생B", 80);
        studentMap.put("학생C", 100);

        // "학생A": 90, "학생B": 80, "학생C": 100


        // TODO 2: "학생A"의 점수를 출력하세요. (힌트: get())
        System.out.println(studentMap.get("학생A"));

        // TODO 3: 모든 학생의 이름과 점수를 출력하세요.
        // 힌트 1: studentMap.keySet()을 사용하여 모든 키(이름)를 먼저 가져온 뒤 루프를 돕니다.
        // 힌트 2: 또는 studentMap.entrySet()을 사용하여 키-값 쌍을 통째로 가져옵니다.
        System.out.println("== 학생 명단 ==");
        System.out.println(studentMap.keySet());
        System.out.println(studentMap.entrySet());


        // TODO 4: "학생B"가 점수를 95점으로 수정했습니다. 기존 값을 95로 덮어쓰세요.
        studentMap.put("학생B", 95);


    }
}