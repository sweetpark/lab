package com.example.playground.wiezon.service;

import com.example.playground.wiezon.context.TemplateContext;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import static com.example.playground.wiezon.util.DataVariableResolver.chgDateValue;
import static com.example.playground.wiezon.util.DataVariableResolver.chgEncValue;

/**
 * JSON 파일을 읽어 파싱하고, 데이터 전처리(날짜 변환, 암호화)를 담당하는 서비스입니다.
 */
@Service
public class FileReadService {

    @Value("${base.datetime}")
    private String baseTime;
    private final DateTimeFormatter baseTimeFormater = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * InputStream에서 JSON 데이터를 읽어 {@link TemplateContext} 객체로 파싱합니다.
     */
    public TemplateContext parseJson(InputStream inputStream){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            Gson gson = new Gson();
            return gson.fromJson(br, TemplateContext.class);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read json file", e);
        }
    }

    /**
     * 메타데이터의 각 행에 대해 전처리를 수행합니다. (날짜 치환, 암호화 등)
     */
    public void dataPreProcess(TemplateContext templateContext){
        LocalDateTime bt;
        try {
            LocalDate date = LocalDate.parse(baseTime, baseTimeFormater);
            bt = date.atTime(LocalTime.now());
        } catch (DateTimeParseException e) {
            throw new RuntimeException("base datetime error (properties 확인 필요)", e);
        }
        preprocessMetaData(templateContext,bt);
    }

    private void preprocessMetaData(TemplateContext templateContext, LocalDateTime bt) {
        templateContext.getRows().forEach(row -> preprocessRow(row, bt));
    }
    private void preprocessRow(Map<String, Map<String, Object>> row, LocalDateTime bt) {
        Map<String, Map<String, Object>> additionalData = new HashMap<>();

        row.entrySet().forEach( entry -> {
            chgEncValue(entry, additionalData);
            chgDateValue(entry, bt);
        });

        row.putAll(additionalData);
    }

}