package com.example.playground.thread.service.java;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/***
 * JAVA ( 하나의 JVM 위에서 작동 )
 * 단일 락
 *
 * 멤버 자원 공유 (count 증가)
 */
@Slf4j
@Service
public class MutexService {

    private final AtomicInteger count = new AtomicInteger(0);
    private final ReentrantLock lock = new ReentrantLock();

    public void updateCount() throws InterruptedException {
        lock.lock();
        try{
            count.incrementAndGet();
            Thread.sleep(1);
        }finally{
            lock.unlock();
        }
    }

    public int getCount(){
        return this.count.get();
    }

}
