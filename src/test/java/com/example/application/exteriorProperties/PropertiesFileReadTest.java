package com.example.application.exteriorProperties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PropertiesFileReadTest {

    @Autowired
    private PropertiesRead propertiesRead;

    @Test
    void printProperties(){
        propertiesRead.printProperties();
    }

}