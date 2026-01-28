package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.util.EncUtil;
import com.google.gson.Gson;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FileReadService {


    public MetaData parseJson(InputStream inputStream){

        MetaData metaData = null;

        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            Gson gson = new Gson();
            metaData = gson.fromJson(br, MetaData.class);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read json file", e);
        }


        return metaData;
    }


    public void dataPreProcess(MetaData metaData){
        List<Map<String, Object>> rows = metaData.getRows();

        for(Map<String, Object> row : rows){

            for(String key : row.keySet()){

                Map<String, Object> data = (Map<String, Object>) row.get(key);

                if(data.get("crypto") != null){
                    switch(data.get("crypto").toString()){
                        case "ENC_HASH":
                            row.put(key+"_ENC", Map.of("value", EncUtil.createEnc(data.get("value").toString())));
                            row.put(key+"_HASH", Map.of("value", EncUtil.createHash(data.get("value").toString())));
                            break;
                        case "OTP":
                            row.put(key, Map.of("value", EncUtil.createEncOtp()));
                        case "PW":
                            row.put(key, Map.of("value", EncUtil.createEncPw(data.get("value").toString())));
                    }
                }


                // COLUMN : DATA
                LocalDateTime now = LocalDateTime.now();
                switch (data.get("value").toString()){
                    case "CUR_YYMMDD":
                        data.put("value", now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                        break;
                    case "CUR_YYMMDDHHIISS":
                        data.put("value", now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
