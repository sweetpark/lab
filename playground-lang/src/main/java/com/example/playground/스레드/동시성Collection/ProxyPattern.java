package com.example.playground.스레드.동시성Collection;

import java.util.List;

public class ProxyPattern {

    // 모둔 List에 대한 메서드에 synchronized를 기입  === Collections.synchronizedList(new ArrayList<>());
    public abstract static class SyncListProxy<T> implements List{
        private final List target; // 진짜 리스트 (ArrayList)

        public SyncListProxy(List target) {
            this.target = target;
        }

        @Override
        public synchronized boolean add(Object o) {
            return target.add(o);
        }

        @Override
        public synchronized Object get(int index) {
            return target.get(index); // 대리인이 락을 걸고 진짜를 호출
        }
    }
}
