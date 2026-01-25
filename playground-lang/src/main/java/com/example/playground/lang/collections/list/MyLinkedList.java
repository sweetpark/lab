package com.example.playground.lang.collections.list;

/*
 * [주제: 직접 구현하는 연결 리스트 (LinkedList)]
 * * 1. 이 주제를 연습하는 이유:
 * - 데이터를 꼬리에 꼬리를 무는 '노드' 단위로 관리하여, 삽입/삭제 시 데이터를 밀지 않아도 되는 원리를 이해하기 위함입니다.
 * * 2. 이걸 왜 사용해야 하는가?:
 * - 리스트의 앞부분에 데이터를 자주 추가하거나 삭제해야 할 때 배열보다 월등히 빠릅니다.
 */

public class MyLinkedList<E> {
    private Node<E> first; // 첫 번째 노드
    private int size = 0;

    // 내부에서만 사용할 노드 클래스 (중첩 클래스 활용!)
    private static class Node<E> {
        E item;
        Node<E> next;

        Node(E item) {
            this.item = item;
        }
    }

    // TODO 1: 데이터를 마지막에 추가하는 add(E e)를 구현하세요.
    public void add(E e) {
        Node<E> newNode = new Node<>(e);
        if (first == null) {
            first = newNode;
        } else {
            // 마지막 노드를 찾아서 연결해야 합니다.
            Node<E> last = getLastNode();
            last.next = newNode;
        }
        size++;
    }

    private Node<E> getLastNode() {
        Node<E> x = first;
        while (x.next != null) {
            x = x.next;
        }
        return x;
    }

    // TODO 2: 특정 인덱스의 노드를 반환하는 getNode(int index)를 구현하세요.
    // 힌트: first부터 시작해서 index만큼 next를 타고 이동해야 합니다.
    private Node<E> getNode(int index) {
        Node<E> x = first;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    }

    // TODO 3: 특정 인덱스의 값을 반환하는 get(int index)를 구현하세요.
    public E get(int index) {
        return getNode(index).item;
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("Java");
        list.add("Spring");
        list.add("JPA");

        System.out.println("== LinkedList 출력 ==");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }
}