package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.util.CryptoType;
import com.example.playground.wiezon.util.EncUtil;
import com.google.gson.Gson;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileReadService {


    public MetaData parseJson(InputStream inputStream){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            Gson gson = new Gson();
            return gson.fromJson(br, MetaData.class);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read json file", e);
        }
    }

    public void dataPreProcess(MetaData metaData){
        preprocessMetaData(metaData,LocalDateTime.now());
    }

    private void preprocessMetaData(MetaData metaData, LocalDateTime now) {
        metaData.getRows().forEach(row -> preprocessRow(row, now));
    }
    private void preprocessRow(Map<String, Map<String, Object>> row, LocalDateTime now) {
        Map<String, Map<String, Object>> additionalData = new HashMap<>();

        row.entrySet().forEach( entry -> {
            chgEncValue(entry, additionalData);
            chgDateValue(entry, now);
        });

        row.putAll(additionalData);
    }
    private static void chgEncValue(Map.Entry<String, Map<String, Object>> entry, Map<String, Map<String, Object>> additionalData) {
        if(entry.getValue().get("crypto") != null){
            CryptoType cryptoType = CryptoType.from(entry.getValue().get("crypto"));
            switch(cryptoType){
                case CryptoType.ENC_HASH -> {
                    additionalData.putIfAbsent(entry.getKey()+ "_ENC" , valueMap(EncUtil.createEnc(entry.getValue().get("value").toString())));
                    additionalData.putIfAbsent(entry.getKey() + "_HASH" , valueMap(EncUtil.createHash(entry.getValue().get("value").toString())));
                }
                case CryptoType.OTP -> entry.setValue(valueMap(EncUtil.createEncOtp()));
                case CryptoType.PASSWORD -> entry.setValue(valueMap(EncUtil.createEnc(entry.getValue().get("value").toString())));
            }
        }
    }
    private static void chgDateValue(Map.Entry<String, Map<String, Object>> entry, LocalDateTime now) {
        if(entry.getValue().get("value") != null){
            switch (entry.getValue().get("value").toString()){
                case "CUR_YYMMDD" -> entry.setValue(valueMap(now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                case "CUR_YYMMDDHHIISS" -> entry.setValue(valueMap(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
            }
        }
    }
    private static @NonNull Map<String, Object> valueMap(String now) {
        Map<String, Object> newDate = new HashMap<>();
        newDate.put("value", now);
        return newDate;
    }
}
