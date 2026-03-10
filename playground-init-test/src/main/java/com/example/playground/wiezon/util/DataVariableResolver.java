package com.example.playground.wiezon.util;

import com.example.playground.wiezon.Enum.CryptoType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.valueMap;

/**
 * 템플릿 데이터 내의 변수(${...})를 실제 값으로 치환해주는 유틸리티 클래스입니다.
 */
public class DataVariableResolver {

    /**
     * row의 모든 컬럼을 순회하며, 값이 ${VARIABLE} 패턴인 경우 variables 맵에서 찾아 치환합니다.
     * <p>
     * 1. variables 맵이 null이거나 비어있으면 예외를 발생시킵니다.
     * 2. row의 각 컬럼 값을 확인하여 문자열이면서 "${"로 시작하고 "}"로 끝나는 경우 치환을 시도합니다.
     * 3. 매칭되는 변수가 variables에 없으면 예외를 발생시킵니다.
     *
     * @param row       JSON에서 읽어온 데이터 row
     * @param variables 치환할 변수 맵 (key: "${VAR}", value: "actualValue")
     * @throws RuntimeException 변환할 맵이 없거나, 매칭되는 변수가 없는 경우
     */
    public static void replace(Map<String, Map<String, Object>> row, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            row.keySet().forEach(
                    key -> System.out.println(row.get(key).toString() + " 변환 할 수 없습니다. ")
            );
            throw new RuntimeException("변환 오류");
        }

        row.forEach((colName, valueMap) -> {
            Object valueObj = valueMap.get("value");
            if (valueObj instanceof String strValue) {
                if (strValue.startsWith("${") && strValue.endsWith("}")) {
                    Object replacement = variables.get(strValue);
                    if (replacement != null) {
                        valueMap.put("value", replacement.toString());
                    }else{
                        throw new RuntimeException("존재하지 않는 strValue : " + strValue);
                    }
                }
            }
        });
    }

    /**
     * 암호화가 필요한 값에 대해 암호화 처리를 수행하고, 추가적인 컬럼(ENC, HASH 등)을 생성합니다.
     */
    public static void chgEncValue(Map.Entry<String, Map<String, Object>> entry, Map<String, Map<String, Object>> additionalData) {
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
    public static void chgDateValue(Map.Entry<String, Map<String, Object>> entry, LocalDateTime now) {
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