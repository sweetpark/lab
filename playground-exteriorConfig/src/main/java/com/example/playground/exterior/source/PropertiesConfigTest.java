package com.example.playground.exterior.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class PropertiesConfigTest {

    @Value("${server.url}")
    private String serverUrl;
    @Value("${server.apiKey}")
    private String apiKey;


    public void init(){
        log.info("server url : {}", serverUrl);
        log.info("server.apiKey : {}", apiKey);
    }


}
