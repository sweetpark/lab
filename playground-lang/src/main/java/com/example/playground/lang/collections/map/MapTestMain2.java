package com.example.playground.lang.collections.map;

import java.util.HashMap;
import java.util.Map;

public class MapTestMain2 {

    public static class Key{
        private Integer key;
        private Integer key2;

        Key(Integer key1, Integer key2){
            this.key = key1;
            this.key2 = key2;
        }

        @Override
        public int hashCode() {
            return key + key2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key keyObj = (Key) o;
            // 두 필드가 모두 같아야 진짜 같은 객체!
            return this.key + this.key2 == keyObj.key + keyObj.key2;
        }
    }

    public static void main(String[] args) {
        Map<Key, String> map = new HashMap<>();


        // key값이 동일하면, 중복된 key로 판단 >> equals() + hashcode() 구현 확인 필요)
        map.put(new Key(10,20), "Test 1");
        map.put(new Key(20,10), "Test 2");

        System.out.println(map.entrySet());
    }
}
