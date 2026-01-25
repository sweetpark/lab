package com.example.playground.lang.collections.iterator;

import java.util.*;

/*
 * [주제: Map의 데이터 순회]
 * 1. 왜 사용하는가?:
 * - Map은 키-값 쌍이라 순서가 없으므로 직접 순회할 수 없습니다.
 * - 따라서 Key만 뽑거나(keySet), 쌍으로 뽑아서(entrySet) 순회해야 합니다.
 */

public class MapIterationMain {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("Apple", 1000);
        map.put("Banana", 2000);
        map.put("Orange", 3000);

        // 1. Key만 뽑아서 순회 (keySet)
        System.out.println("== KeySet 활용 ==");
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            System.out.println("Key: " + key + ", Value: " + map.get(key));
        }

        // 2. Key-Value 쌍으로 뽑아서 순회 (entrySet) - 실무 권장 (성능상 유리)
        System.out.println("\n== EntrySet 활용 ==");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }
}