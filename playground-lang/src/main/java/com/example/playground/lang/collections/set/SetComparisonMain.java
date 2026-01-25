package com.example.playground.lang.collections.set;

import java.util.*;

/*
 * [주제: 자바가 제공하는 다양한 Set 인터페이스 구현체]
 * * 1. HashSet: 순서를 보장하지 않음. 해시 알고리즘 사용 (가장 빠름 O(1))
 * * 2. LinkedHashSet: 넣은 순서를 보장함. (해시 + 연결 리스트 사용)
 * * 3. TreeSet: 데이터의 '값'에 따라 정렬됨. (레드-블랙 트리 사용 O(log n))
 */

public class SetComparisonMain {
    public static void main(String[] args) {
        // TODO 1: 아래 데이터들을 HashSet, LinkedHashSet, TreeSet에 각각 넣고 출력해보세요.
        // 데이터: "B", "A", "C", "1", "2"
        Set<String> hashSet = new HashSet<>();
        Set<String> linked = new LinkedHashSet<>();
        Set<String> treeSet = new TreeSet<>();

        System.out.println("== HashSet (순서 보장 X) ==");
        hashSet.add("B");
        hashSet.add("A");
        hashSet.add("C");
        hashSet.add("1");
        hashSet.add("2");

        System.out.println(hashSet);

        // 여기에 작성 (Set<String> hashSet = ...)

        System.out.println("\n== LinkedHashSet (넣은 순서 보장) ==");
        linked.add("B");
        linked.add("A");
        linked.add("C");
        linked.add("1");
        linked.add("2");

        System.out.println(linked);
        // 여기에 작성

        System.out.println("\n== TreeSet (값에 따른 정렬 - 오름차순) ==");
        treeSet.add("B");
        treeSet.add("A");
        treeSet.add("C");
        treeSet.add("1");
        treeSet.add("2");

        System.out.println(treeSet);

        System.out.println("\n == TreeSet (내림차순) ");
        Set<String> descTreeSet = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });

        descTreeSet.addAll(treeSet);

        System.out.println(descTreeSet);
    }
}