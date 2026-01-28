package com.example.playground.wiezon.util;

import kms.wiezon.com.crypt.CryptUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoUtil {

    public static CryptUtils getCryptoUtils(){
        return new CryptUtils();
    }

}
