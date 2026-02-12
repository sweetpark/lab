package com.example.playground.wiezon;


import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.InitDataAssembler;
import com.example.playground.wiezon._enum.CryptoType;
import com.example.playground.wiezon.util.EncUtil;
import com.google.gson.Gson;
import kms.wiezon.com.crypt.CryptUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.ActiveProfiles;

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
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
public class InitTest {

    @Autowired
    public ResourcePatternResolver resolver;
    @Autowired
    Environment environment;
    @Autowired
    InitDataAssembler assembler;

    @Autowired
    public DataSource dataSource;
    public CryptUtils cryptUtils;

    @Value("${mid}")
    private String prefixMidName;



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
            System.out.println(EncUtil.createEnc("1000000"));
            Assertions.assertInstanceOf(String.class,EncUtil.createEnc("data"));
        }catch(Exception e){
            Assertions.fail("암호화 실패");
        }
    }


    @Test
    @DisplayName("file 읽기")
    public void fileRead(){

        try {
            Resource[] resources = resolver.getResources("classpath:/data/test/*.json");

            for(Resource resource : resources){
                System.out.println(resource.getURI());

                InputStream read = resource.getInputStream();
                Gson gson = new Gson();

                BufferedReader br = new BufferedReader(new InputStreamReader(read));
                MetaData metaData = gson.fromJson(br, MetaData.class);
                Assertions.assertEquals("TEST_TABLE", metaData.getTable());
                if(!metaData.getRows().isEmpty()){
                    Map<String, Map<String, Object>> first = metaData.getRows().getFirst();
                    Assertions.assertEquals("data", first.get("ID").get("value"));
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

    @Test
    @DisplayName("매장ID 생성로직")
    void mid_create_test(){
        int number = 10;

        List<String> midList  = new ArrayList<>();
        for(int i = 0; i < number; i++){
            String sb = prefixMidName +
                    String.format("%03d", i) +
                    "m";

            midList.add(sb);
        }

        Assertions.assertTrue(midList.contains("sitest001m"));

    }

    @Test
    @DisplayName("사업자번호 생성 로직")
    void create_co_no() {
        int length = 10;
        List<String> coNoNum = new ArrayList<>();
        Random random = new Random();

        while (coNoNum.size() != 10) {
            for (int i = 0; i < length; i++) {
                coNoNum.add(String.valueOf(random.nextInt(10)));
            }
            if (!validateCoNo(coNoNum)) {
                coNoNum.clear();
            }
        }

        System.out.println("Generated Business Number: " + String.join("", coNoNum));
        Assertions.assertEquals(10, coNoNum.size());
    }

    @Test
    @DisplayName("제휴사 코드 변경")
    void create_cpid() throws IOException {
        Resource[] resources = resolver.getResources("classpath:/data/**/*.json");

        Arrays.stream(resources).forEach(resource -> {
                MetaData template = parse(resource);
                int index = 0;
                while(true){
                    String ptnCd = environment.getProperty(String.format("cpids[%d].ptnCd", index));
                    if(ptnCd == null){
                        break;
                    }
                    String keyType = environment.getProperty(String.format("cpids[%d].keyType", index));
                    String key = environment.getProperty(String.format("cpids[%d].key", index));


                    List<Map<String, Map<String, Object>>> newRows = template.getRows().stream()
                            .map(templateRow -> {

                                Map<String, Map<String, Object>> deepRow = new HashMap<>();

                                // 깊은 복사
                                for (String _key : templateRow.keySet()) {
                                    Map<String, Object> innerMap = templateRow.get(_key);
                                    deepRow.put(_key, new HashMap<>(innerMap));
                                }

                                if (isTemplateMatched(deepRow, "PTN_CD", "${PTN_CD}")
                                        && isTemplateMatched(deepRow, "KEY_TYPE", "${KEY_TYPE}")
                                        && isTemplateMatched(deepRow, "DATA", "${KEY}"))
                                {
                                    deepRow.put("PTN_CD", valueMap(ptnCd));
                                    deepRow.put("KEY_TYPE", valueMap(keyType));
                                    String delimiter = deepRow.get("DELIMITER").get("value").toString();
                                    Assertions.assertNotNull(key);
                                    String[] keys = key.split(",");

                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < keys.length; i++) {
                                        if(i == keys.length -1){
                                            sb.append(keys[i]);
                                        }else{
                                            sb.append(keys[i]).append(delimiter);
                                        }
                                    }

                                    deepRow.put("DATA", valueMap(sb.toString()));
                                }else if(isTemplateMatched(deepRow, "PTN_CD", "${PTN_CD}")){
                                    deepRow.put("PTN_CD", valueMap(ptnCd));
                                }

                                return deepRow;
                            })
                            .collect(Collectors.toList());


                    MetaData newMetaData = new MetaData(template.getTable(), newRows);
                    Assertions.assertFalse(newMetaData.getRows().isEmpty());
                    System.out.println(newMetaData.getRows());

                    index++;
                }
        });
    }



    @Test
    @DisplayName("영세 사업자 번호 가져오기")
    void getCoNo(){

        String coNo = getCoNo(dataSource);

        Assertions.assertNotNull(coNo);
        Assertions.assertEquals(10, coNo.length());
        System.out.println("CO_NO : " + coNo);
    }

    @Test
    @DisplayName("초기 데이터 셋팅")
    void initData() throws SQLException {

        InitData initData = assembler.assemble();

        Assertions.assertEquals(1, initData.getCpidList().size());
        Assertions.assertEquals(1, initData.getMidList().size());
        initData.getCpidList().forEach(System.out::println);
        initData.getMidList().forEach(System.out::println);
    }

    private boolean isTemplateMatched(Map<String, Map<String, Object>> row, String colKey, String templateStr) {
        if (!row.containsKey(colKey) || row.get(colKey) == null) return false;

        Object valueObj = row.get(colKey).get("value");
        return valueObj != null && valueObj.equals(templateStr);
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
    private boolean validateCoNo(List<String> coNoNum) {
        if (coNoNum.size() != 10) {
            return false;
        }

        int[] digits = new int[10];
        for (int i = 0; i < 10; i++) {
            digits[i] = Integer.parseInt(coNoNum.get(i));
        }

        int[] weights = {1, 3, 7, 1, 3, 7, 1, 3, 5};
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += digits[i] * weights[i];
        }

        int lastWeightDigit = digits[8] * weights[8];
        sum += (lastWeightDigit / 10) + (lastWeightDigit % 10);

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == digits[9];
    }
    private static String getCoNo(DataSource dataSource) {
        String sql =
                "SELECT tmma.CO_NO " +
                        "FROM TBSI_MS_MBS_ALL tmma " +
                        "LEFT JOIN TBSI_CO tsc ON tmma.CO_NO = tsc.CO_NO " +
                        "WHERE tmma.SM_MBS_CD = ? " +
                        "AND tsc.CO_NO IS NULL " +
                        "ORDER BY tmma.TO_DT DESC " +
                        "LIMIT 1";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, "A1");

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return rs.getString("CO_NO");
                }else{
                    throw new RuntimeException("영세를 가진 사업자번호가 존재하지 않습니다.");
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
