package com.example.playground.crolling;

import com.example.playground.crolling.source.Crolling;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrollingTest {

    @Autowired
    private Crolling crolling;

    @Test
    void 크롤링(){
        crolling.start();
    }

}