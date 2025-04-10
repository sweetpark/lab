package com.example.application.crolling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CrollingTest {

    @Autowired
    private Crolling crolling;

    @Test
    void 크롤링(){
        crolling.start();
    }

}