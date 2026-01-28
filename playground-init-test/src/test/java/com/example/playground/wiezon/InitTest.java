package com.example.playground.wiezon;


import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.util.EncUtil;
import com.google.gson.Gson;
import kms.wiezon.com.crypt.CryptUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@SpringBootTest
public class InitTest {

    @Autowired
    public ResourcePatternResolver resolver;
    public CryptUtils cryptUtils;

    @BeforeEach
    public void init(){
        cryptUtils = new CryptUtils();
    }

    @Test
    @DisplayName("암호화 실패 테스트")
    public void encryptFailTest(){
        Assertions.assertThrows(Exception.class, () -> {
            EncUtil.createEnc("data");
        });
    }


    @Test
    @DisplayName("암호화 성공 테스트")
    public void encSuccessTest() throws Exception {
        try{
            Assertions.assertInstanceOf(String.class,EncUtil.createEnc("data"));
        }catch(Exception e){
            Assertions.fail("암호화 실패");
        }
    }


    @Test
    @DisplayName("file 읽기")
    public void fileRead(){

        try {
            Resource[] resources = resolver.getResources("classpath:/data/**/*.json");

            for(Resource resource : resources){
                InputStream read = resource.getInputStream();
                Gson gson = new Gson();

                BufferedReader br = new BufferedReader(new InputStreamReader(read));
                MetaData metaData = gson.fromJson(br, MetaData.class);
                Assertions.assertEquals(metaData.getTable(), "table");
                if(!metaData.getRows().isEmpty()){
                    Map<String, Object> first = metaData.getRows().getFirst();
                    Assertions.assertEquals(first.get("value"), "data");
                }
            }
        } catch (IOException e) {
            Assertions.fail("파일 read 실패");
        }

    }




}
