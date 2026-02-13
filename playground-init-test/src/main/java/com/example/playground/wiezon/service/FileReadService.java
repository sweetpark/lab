package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon._enum.CryptoType;
import com.example.playground.wiezon.util.EncUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.valueMap;

/**
 * JSON 파일을 읽어 파싱하고, 데이터 전처리(날짜 변환, 암호화)를 담당하는 서비스입니다.
 */
@Service
public class FileReadService {


    /**
     * InputStream에서 JSON 데이터를 읽어 {@link MetaData} 객체로 파싱합니다.
     */
    public MetaData parseJson(InputStream inputStream){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            Gson gson = new Gson();
            return gson.fromJson(br, MetaData.class);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read json file", e);
        }
    }

    /**
     * 메타데이터의 각 행에 대해 전처리를 수행합니다. (날짜 치환, 암호화 등)
     */
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

    /**
     * 암호화가 필요한 값에 대해 암호화 처리를 수행하고, 추가적인 컬럼(ENC, HASH 등)을 생성합니다.
     */
    private static void chgEncValue(Map.Entry<String, Map<String, Object>> entry, Map<String, Map<String, Object>> additionalData) {
        if(entry.getValue().get("crypto") != null){
            CryptoType cryptoType = CryptoType.from(entry.getValue().get("crypto"));
            switch(cryptoType){
                case CryptoType.ENC_HASH -> {
                    additionalData.putIfAbsent(entry.getKey()+ "_ENC" , valueMap(EncUtil.createEnc(entry.getValue().get("value").toString())));
                    additionalData.putIfAbsent(entry.getKey() + "_HASH" , valueMap(EncUtil.createHash(entry.getValue().get("value").toString())));
                }
                case CryptoType.OTP -> entry.setValue(valueMap(EncUtil.createEncOtp()));
                case CryptoType.PASSWORD -> entry.setValue(valueMap(EncUtil.Base64EncodedMD5(entry.getValue().get("value").toString())));
                case CryptoType.ENC_BASE64 -> entry.setValue(valueMap(EncUtil.Base64EncodedMD5(EncUtil.makeRandomPw())));
            }
        }
    }

    /**
     * 날짜 플레이스홀더(CUR_YYMMDD 등)를 실제 날짜 값으로 치환합니다.
     */
    private static void chgDateValue(Map.Entry<String, Map<String, Object>> entry, LocalDateTime now) {
        if(entry.getValue().get("value") != null){
            switch (entry.getValue().get("value").toString()){
                case "CUR_YYMMDD" -> entry.setValue(valueMap(now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                case "CUR_YYMMDDHHIISS" -> entry.setValue(valueMap(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
                case "CUR_HHIISS" -> entry.setValue(valueMap(now.format(DateTimeFormatter.ofPattern("HHmmss"))));
                case "YESTER_YYMMDD" -> entry.setValue(valueMap(now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                case "YESTER_YYMMDDHHIISS" -> entry.setValue(valueMap(now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
                case "YESTER_HHIISS" -> entry.setValue(valueMap(now.minusDays(1).format(DateTimeFormatter.ofPattern("HHmmss"))));
            }
        }
    }

}