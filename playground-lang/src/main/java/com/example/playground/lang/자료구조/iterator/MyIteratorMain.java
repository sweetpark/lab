package com.example.playground.lang.자료구조.iterator;

import java.util.Iterator;

/*
 * [주제: 직접 구현하는 순회 (Iterable & Iterator)]
 * 1. 이 주제를 연습하는 이유:
 * - 내부 구조(배열, 노드, 트리 등)가 무엇이든 외부에서는 동일한 인터페이스(next, hasNext)로
 * 데이터를 읽을 수 있게 하기 위함입니다. (추상화의 극치!)
 * 2. 이걸 왜 사용하는가?:
 * - 자료구조를 바꿔도(ArrayList -> LinkedList) 데이터를 읽는 반복문 코드를 수정할 필요가 없기 때문입니다.
 */

class MyData implements Iterable<String> {
    private String[] items;

    public MyData(String[] items) {
        this.items = items;
    }

    // Iterable 인터페이스의 약속: "나를 순회할 일꾼(Iterator)을 반환해라"
    @Override
    public Iterator<String> iterator() {
        return new MyDataIterator(items);
    }
}

class MyDataIterator implements Iterator<String> {
    private int cursor = 0;
    private String[] target;

    public MyDataIterator(String[] target) {
        this.target = target;
    }

    // 다음 데이터가 있는지 확인하는 로직
    @Override
    public boolean hasNext() {
        return cursor < target.length;
    }

    // 데이터를 하나 꺼내오고 다음 위치로 커서를 옮기는 로직
    @Override
    public String next() {
        return target[cursor++];
    }
}

public class MyIteratorMain {
    public static void main(String[] args) {
        MyData myData = new MyData(new String[]{"Java", "Spring", "JPA"});

        // Iterable을 구현했기 때문에 이제 향상된 for문을 쓸 수 있습니다.
        System.out.println("== 향상된 for문 순회 ==");
        for (String s : myData) {
            System.out.println(s);
        }
    }
}