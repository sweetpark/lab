package com.example.playground.lang.자료구조.list;

import java.util.Arrays;

/*
 * [주제: 직접 구현하는 배열 리스트 (ArrayList)]
 * * 1. 이 주제를 연습하는 이유:
 * - 자바가 제공하는 ArrayList가 내부적으로 어떻게 동작(동적 확장, 인덱스 접근)하는지 이해하기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 일반 배열은 크기가 고정되어 불편하지만, ArrayList는 데이터 양에 따라 유동적으로 변하므로 실무에서 가장 많이 쓰입니다.
 * * 3. 어떤 경우에 사용되는가?:
 * - 데이터의 개수를 미리 알 수 없을 때, 인덱스로 빠른 조회가 필요할 때 사용합니다.
 */

/***
 지금 만드신 MyArrayList는 조회(get) 속도가 매우 빠릅니다 ($O(1)$).
    하지만 중간에 데이터를 넣거나 뺄 때는 어떨까요?

 1. 중간 삽입: 특정 위치 뒤에 있는 모든 데이터를 한 칸씩 뒤로 밀어야 합니다.
 2. 중간 삭제: 특정 위치 뒤에 있는 모든 데이터를 한 칸씩 앞으로 당겨야 합니다.
    결과: 데이터가 100만 개인데 0번 인덱스에 데이터를 넣으려면 100만 번의 이동이 발생합니다 ($O(n)$).
 */

public class MyArrayList<E> {
    private static final int DEFAULT_CAPACITY = 2; // 테스트를 위해 작게 설정
    private Object[] elementData;
    private int size = 0;

    public MyArrayList() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    // TODO 1: 데이터를 추가하는 add(E e) 메서드를 완성하세요.
    // 힌트: 배열이 가득 찼는지 확인하고(size == length), 가득 찼다면 grow()를 호출하세요.
    public void add(E e) {
        if(size >= this.elementData.length -1){
            grow();
        }
        elementData[size++] = e;
    }

    // TODO 2: 배열의 크기를 2배로 늘리는 grow() 메서드를 완성하세요.
    private void grow() {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity * 2;

        // 힌트: Arrays.copyOf(기존배열, 새길이)를 사용하면 데이터 복사와 크기 증가가 동시에 가능합니다.
        // elementData = ...
        elementData = Arrays.copyOf(elementData, newCapacity);

        System.out.println("Capacity 증가: " + oldCapacity + " -> " + newCapacity);
    }

    // TODO 3: 특정 인덱스의 데이터를 반환하는 get(int index) 메서드를 완성하세요.
    @SuppressWarnings("unchecked")
    public E get(int index) {
        // 힌트: 내부 배열은 Object[]이므로 (E)로 형변환하여 반환해야 합니다.
        return elementData[index] != null ? (E) elementData[index] : null;
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        MyArrayList<String> list = new MyArrayList<>();

        System.out.println("== 데이터 추가 ==");
        list.add("Java");
        list.add("Spring"); // 여기서 grow() 호출되어야 함
        list.add("JPA");    // 여기서 grow() 호출되어야 함

        System.out.println("== 데이터 출력 ==");
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Index " + i + ": " + list.get(i));
        }
    }
}
