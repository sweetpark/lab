package com.example.playground.스레드.동시성Collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;

public class CollectionMain {
    // [문제 1] 실무에서 가장 많이 쓰는 동시성 Map 구현체입니다.
    // 설명: Java 동시성 기술의 집약체입니다.
    // 왜 사용함?: 일반 HashMap은 동시 접근 시 깨지고, Hashtable은 너무 느립니다.
    // 이 클래스는 맵 전체를 잠그는 게 아니라, 데이터가 들어있는 '구역(Segment/Bucket)'만 쪼개서 잠그기 때문에
    // 여러 스레드가 동시에 다른 구역에 값을 넣을 수 있습니다. (성능 최강)
    private static Map<String, Integer> map = new ConcurrentHashMap<>();

    // [문제 2] 읽기는 락 없이 아주 빠르고, 쓸 때만 전체를 복사해서 갈아끼우는 리스트입니다.
    // 설명: "쓸 때(Write) 복사한다(Copy)"는 뜻의 이름입니다.
    // 왜 사용함?: 이벤트 리스너 목록처럼 '등록(쓰기)'은 가끔 일어나고 '조회(읽기)'가 엄청 자주 일어날 때,
    // synchronizedList보다 압도적으로 빠릅니다. (반대로 쓰기가 많으면 느려서 안 씁니다)
    private static List<String> list = new CopyOnWriteArrayList<>();

    // 쓰기가 많다면 Collections.synchronizedList(new ArrayList<>())사용
    // copy하는데 더욱 많은 리소스를 사용해서 오히려 성능이 내려갈 수 있으므로
    //private static List<String> list = Collections.synchronizedList(new ArrayList<>());


    public static void main(String[] args) {
        // 사용법은 일반 Map, List와 100% 동일합니다.
        map.put("key", 1);
        list.add("data");

        System.out.println("동시성 문제 없이 안전함: " + map.get("key"));
    }
}
