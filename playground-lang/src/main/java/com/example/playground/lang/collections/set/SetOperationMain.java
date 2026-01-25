package com.example.playground.lang.collections.set;

import java.util.*;

public class SetOperationMain {
    public static void main(String[] args) {
        Set<Integer> set1 = new HashSet<>(List.of(1, 2, 3, 4, 5));
        Set<Integer> set2 = new HashSet<>(List.of(3, 4, 5, 6, 7));

        // TODO 2: 두 집합의 합집합을 구하세요. (힌트: addAll)
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        // 코드 작성

        // TODO 3: 두 집합의 교집합을 구하세요. (힌트: retainAll)
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        // 코드 작성

        // TODO 4: 두 집합의 차집합(set1 - set2)을 구하세요. (힌트: removeAll)
        Set<Integer> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        // 코드 작성

        System.out.println("합집합: " + union);
        System.out.println("교집합: " + intersection);
        System.out.println("차집합: " + difference);
    }
}