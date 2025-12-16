package com.example.playground.exteriorProperties;


import com.example.playground.exterior.source.PropertiesConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PropertiesConfigTestTest {

    @Autowired
    private PropertiesConfigTest test;

    @Test
    void 테스트(){
        test.init();
    }
}