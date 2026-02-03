package com.example.playground.wiezon.config;

import kms.wiezon.com.crypt.CryptUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


import java.lang.reflect.Field;

@Configuration
@PropertySource("classpath:kms.properties")
public class KmsConfig {

    @Value("${SEND_IP}")
    private String sendIp;

    @Value("${REMOTE_IP}")
    private String remoteIp;

    @Value("${REMOTE_PORT}")
    private String remotePort;

    @Value("${REMOTE_TIMEOUT}")
    private String remoteTimeout;

    @Bean
    public CryptUtils cryptUtils() {
        CryptUtils cryptUtils = new CryptUtils();
        try {
            setField(cryptUtils, "sendIP", sendIp);
            setField(cryptUtils, "remoteIP", remoteIp);
            setField(cryptUtils, "remotePORT", remotePort);
            setField(cryptUtils, "remoteTIMEOUT", remoteTimeout);
        } catch (Exception e) {
            throw new RuntimeException("Error configuring CryptUtils", e);
        }
        return cryptUtils;
    }



    private void setField(Object target, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
