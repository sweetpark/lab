package com.example.playground.wiezon;


import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.util.CryptoType;
import com.example.playground.wiezon.util.EncUtil;
import com.google.gson.Gson;
import kms.wiezon.com.crypt.CryptUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class InitTest {

    @Autowired
    public ResourcePatternResolver resolver;
    @Autowired
    public DataSource dataSource;
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
                Assertions.assertEquals(metaData.getTable(), "TEST_TABLE");
                if(!metaData.getRows().isEmpty()){
                    Map<String, Map<String, Object>> first = metaData.getRows().getFirst();
                    Assertions.assertEquals(first.get("ID").get("value"), "data");
                }
            }
        } catch (IOException e) {
            Assertions.fail("파일 read 실패");
        }

    }


    @Test
    @DisplayName("json 전처리")
    void preProcess(){


        try {
            Resource[] resources = resolver.getResources("classpath:/data/**/*.json");

            for (Resource resource : resources) {
                Gson gson = new Gson();

                // parse
                MetaData metaData = parse(resource);

                // preprocess
                preprocessMetaData(metaData,LocalDateTime.now());

                String json = writeResult(gson, metaData);
                Assertions.assertFalse(json.isEmpty());
            }
        }catch(Exception e){
            Assertions.fail("전처리 실패");
        }
    }

    @Test
    @DisplayName("DB connection")
    void dbConnection(){

        try(Connection con = dataSource.getConnection()){
            Resource[] resources = resolver.getResources("classpath:/data/**/*.json");

            con.setAutoCommit(false);

            for(Resource resource : resources){
                MetaData metaData = parse(resource);
                preprocessMetaData(metaData, LocalDateTime.now());
                dbInsert(metaData,con);
            }

            con.commit();

        }catch(Exception e){
            Assertions.fail();
        }

    }

    private static void dbInsert(MetaData metaData, Connection con) {
        String table = metaData.getTable();

        for(Map<String, Map<String,Object>> row : metaData.getRows()){
            // arrayList 순서보장
            List<String> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            row.forEach((column, map) -> {
                columns.add(column);
                values.add(map.get("value").toString());
            });

            String sql = "INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES ("
                    + values.stream().map(map -> "?").collect(Collectors.joining(", ")) +")";


            try(PreparedStatement pstmt = con.prepareStatement(sql)){

                for (int i = 0; i < values.size(); i++) {
                    pstmt.setObject(i + 1, values.get(i));
                }

                pstmt.executeUpdate();
            }catch(Exception e){
                Assertions.fail();
            }

        }
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
    private MetaData parse(Resource resource){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            Gson gson = new Gson();
            return gson.fromJson(br, MetaData.class);
        }catch(Exception e){
            throw new RuntimeException("parse failed");
        }
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
    private static @NonNull String writeResult(Gson gson, MetaData metaData) throws IOException {
        String json = gson.toJson(metaData);
        Path path = Path.of("./output.txt");
        Files.writeString(path,
                json,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
        return json;
    }


}
