package com.example.playground.wiezon.service;

import com.example.playground.wiezon.ToolRunner;
import com.example.playground.wiezon.context.*;
import com.example.playground.wiezon.Enum.PaymentDetailType;
import com.example.playground.wiezon.Enum.PaymentMethod;
import com.example.playground.wiezon.exception.PayDataCreateException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 초기화에 필요한 기본 데이터(InitData)를 조립하는 서비스입니다.
 */
@Service
public class InitDataAssembler {


    private final DataSource dataSource;
    private final Environment environment;
    private final Set<String> sessionTids = new HashSet<>();

    public InitDataAssembler(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    public Stream<MidContext> streamMids(GlobalContext globalContext, List<CpidContext> cpidContexts){
        // default) 제휴그룹 (cpid) 당 1개 mid
        int midPerCpid = environment.getProperty("mids.per.cpid", Integer.class, 1);

        // mid prefix 방어로직
        String midPrefix = environment.getProperty("mid", "sitest");
        if(midPrefix.length() != 6){
            midPrefix = midPrefix.substring(0, 6);
        }
        String finalMidPrefix = midPrefix;

        // mid 중복 방지
        AtomicInteger seq = new AtomicInteger(0);

        return cpidContexts.stream().flatMap(cpid -> IntStream.range(0, midPerCpid).mapToObj(index -> {
                            MidContext midData = new MidContext();

                            // Global Context 설정
                            midData.setCono(globalContext.getCono());
                            midData.setGid(globalContext.getGid());
                            midData.setL1Vid(globalContext.getL1Vid());
                            midData.setCrctPtnCd(globalContext.getCrctPtnCd());
                            midData.setCrctCpid(globalContext.getCrctCpid());
                            midData.setCrctKeyType(globalContext.getCrctKeyType());
                            midData.setCrctKey(globalContext.getCrctKey());

                            // cpid 맵핑
                            midData.setCpidContext(cpid);

                            //mid 설정 (총 1000개까지 생성 가능)
                            int uniqueIdx = seq.incrementAndGet();
                            midData.setMid(String.format("%s%03dm", finalMidPrefix, uniqueIdx));

                            return midData;
                        }
                )
        );
    }

    public Stream<PayContext> streamPayData(MidContext midContext){
        return IntStream.range(0,100).mapToObj(index -> {
            try{
                int day = (index > 50) ? 1 : 0;
                PayContext payContext = new PayContext();

                payContext.setSpmCd(PaymentDetailType.CARD_AUTH.getDetailCode());
                payContext.setTid1(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midContext, day));
                payContext.setTid2(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midContext, day));
                payContext.setTid1P1(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midContext, day));
                payContext.setTid1P2(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midContext, day));
                payContext.setTid1P3(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midContext, day));
                payContext.setAppNo1(String.format("%08d",ToolRunner.app_no++));
                payContext.setAppNo2(String.format("%08d",ToolRunner.app_no++));
                payContext.setAppNo3(String.format("%08d",ToolRunner.app_no++));

                return payContext;
            }catch(SQLException e){
                throw new PayDataCreateException("payData create Exception! ",e);
            }
        });
    }


    public GlobalContext getGlobalContext() {
        return new GlobalContext(
                getCoNo(dataSource),
                environment.getProperty("gid"),
                environment.getProperty("l1_vid"),
                environment.getProperty("crct.ptnCd"),
                environment.getProperty("crct.cpid"),
                environment.getProperty("crct.keyType"),
                environment.getProperty("crct.key")
        );
    }

    /*
     사용자님께서 말씀하신 "Property에서 식별값(PK)을 읽고 -> 그 PK로 DB를 조회하여 -> 실제 컨텍스트 객체를 완성하는 구조"가 설정 기반의 유연성과 데이터의 정확성을 모두
  잡는 가장 정석적인 방법입니다.


  이 구조를 Stream 기반의 OOM 방지 전략과 결합하여 InitDataAssembler에 적용하는 구체적인 구현 방식을 제안해 드립니다.


  1. 전반적인 프로세스 흐름
   1. `Environment`: 설정 파일(application.yml 등)에서 global.pk, cpids[0].pk 등을 읽음.
   2. `DB Query`: 읽어온 PK를 사용해 SELECT * FROM ... WHERE PK = ? 쿼리 실행.
   3. `Context Mapping`: DB 결과(ResultSet)를 GlobalContext, CpidContext 객체에 매핑.
   4. `Stream Processing`: MidContext는 개수가 많을 수 있으므로, DB에서 Cursor 방식으로 읽어와 Stream으로 흘려보냄.

  2. InitDataAssembler 수정 제안


    1 @Service
    2 public class InitDataAssembler {
    3     private final DataSource dataSource;
    4     private final Environment environment;
    5
    6     // 1. GlobalContext: 설정의 PK로 DB 조회
    7     public GlobalContext getGlobalContext() {
    8         String pk = environment.getProperty("global.pk"); // 설정에서 PK 로드
    9         return findGlobalFromDb(pk); // DB 조회 후 객체 반환
   10     }
   11
   12     // 2. CpidContext: 설정의 PK 리스트로 DB 조회
   13     public List<CpidContext> getBaseCpids() {
   14         // 설정에서 cpids[0].pk, cpids[1].pk 등을 가져옴
   15         List<String> pks = getCpidPksFromConfig();
   16
   17         return pks.stream()
   18                   .map(this::findCpidFromDb) // 각 PK별 최신 DB 상태 로드
   19                   .collect(Collectors.toList());
   20     }
   21
   22     // 3. MidContext: OOM 방지를 위해 Stream으로 반환
   23     public Stream<MidContext> streamMids(GlobalContext global, List<CpidContext> cpids) {
   24         // 여기서도 필요한 경우 DB에서 페이징이나 커서 방식으로 MID 정보를 가져옴
   25         // 예: SELECT * FROM TB_MID WHERE CPID_ID IN (...)
   26         return cpids.stream().flatMap(cpid -> {
   27             // 특정 CPID에 속한 MID들을 DB에서 하나씩 읽어오는 Stream 반환
   28             return fetchMidStreamByCpid(global, cpid);
   29         });
   30     }
   31
   32     // --- DB 조회 상세 로직 (JdbcTemplate 또는 직접 쿼리) ---
   33
   34     private GlobalContext findGlobalFromDb(String pk) {
   35         // SELECT * FROM TB_GLOBAL WHERE ID = pk 쿼리 실행 로직
   36         // rs.getString("CONO"), rs.getString("GID") 등을 GlobalContext에 세팅
   37     }
   38
   39     private Stream<MidContext> fetchMidStreamByCpid(GlobalContext global, CpidContext cpid) {
   40         // JdbcTemplate.queryForStream 등을 사용하여 커서를 유지하며 데이터 로드
   41         // 각 Row를 읽을 때마다 MidContext를 생성하고 global/cpid 정보를 주입
   42     }
   43 }

  3. 왜 이 단계(Assembler)에서 처리하는 것이 좋을까?


   1. `ToolRunner`의 순수성: ToolRunner는 전체적인 실행 흐름(Global -> Cpid -> Mid 순서)만 관리하고, 구체적으로 데이터를 어떻게 가져오는지는 Assembler가 담당하게 됩니다.
   2. 최신성 보장: getGlobalContext()나 findCpidFromDb()를 호출하는 시점이 run() 메서드 내부이므로, 프로그램 실행 직전의 DB 상태를 반영할 수 있습니다.
   3. 메모리 효율: streamMids 내에서 fetchMidStreamByCpid를 통해 데이터를 하나씩 가져오면, DB에 MID가 만 개 이상 있더라도 메모리에는 한 번에 하나의 MidContext만 올라가게
      되어 OOM으로부터 안전합니다.

     */
    public List<CpidContext> getBaseCpids(){
        List<CpidContext> cpidContexts = new ArrayList<>();
        int index = 0;
        while (existsCpidsIndex(environment, index)) {

            CpidContext cpidContext = new CpidContext();

            //인증
            cpidContext.setCertPtnCd(prop("cpids[%d].cert.ptnCd", index));
            cpidContext.setCertCpid(prop("cpids[%d].cert.cpid", index));
            cpidContext.setCertKeyType(prop("cpids[%d].cert.keyType", index));
            cpidContext.setCertKey(addDelimiterKey(prop("cpids[%d].cert.key", index)));

            //구인증
            cpidContext.setOldCertPtnCd(prop("cpids[%d].old.cert.ptnCd", index));
            cpidContext.setOldCertCpid(prop("cpids[%d].old.cert.cpid", index));
            cpidContext.setOldCertKeyType(prop("cpids[%d].old.cert.keyType", index));
            cpidContext.setOldCertKey(addDelimiterKey(prop("cpids[%d].old.cert.key", index)));

            //비인증
            cpidContext.setNoCertPtnCd(prop("cpids[%d].no.cert.ptnCd", index));
            cpidContext.setNoCertCpid(prop("cpids[%d].no.cert.cpid", index));
            cpidContext.setNoCertKeyType(prop("cpids[%d].no.cert.keyType", index));
            cpidContext.setNoCertKey(addDelimiterKey(prop("cpids[%d].no.cert.key", index)));

            //오프라인
            cpidContext.setOfflinePtnCd(prop("cpids[%d].offline.ptnCd", index));
            cpidContext.setOfflineCpid(prop("cpids[%d].offline.cpid", index));
            cpidContext.setOfflineKeyType(prop("cpids[%d].offline.keyType", index));
            cpidContext.setOfflineKey(addDelimiterKey(prop("cpids[%d].offline.key", index)));


            cpidContexts.add(cpidContext);
            index++;
        }

        return cpidContexts;
    }

    public void clearTids(){
        sessionTids.clear();
    }
    private String getCoNo(DataSource dataSource) {

        Connection con = DataSourceUtils.getConnection(dataSource);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = LocalDateTime.now().format(formatter);

        String sql =
                "SELECT tsmm.CO_NO " +
                        "FROM TBSI_MS_MBS tsmm " +
                        "LEFT JOIN TBSI_CO tsc ON tsmm.CO_NO = tsc.CO_NO " +
                        "WHERE tsmm.SM_MBS_CD = ? " +
                        "AND tsc.CO_NO IS NULL " +
                        "AND ? BETWEEN tsmm.FR_DT AND tsmm.TO_DT " +
                        "ORDER BY tsmm.TO_DT DESC " +
                        "LIMIT 1";

        try(PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, "A1");
            pstmt.setString(2, now);

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
    private String prop(String key, int index){
        return environment.getProperty(String.format(key, index));
    }
    private String addDelimiterKey(String preKey){
        if(preKey != null){
            return Arrays.stream(preKey.split(","))
                    .map(String::trim)
                    .collect(Collectors.joining("____TEST____"));
        }else{
            return "";
        }
    }
    private boolean existsCpidsIndex(Environment env, int index) {
        String[] keys = {
                "cpids[%d].cert.ptnCd",
                "cpids[%d].old.cert.ptnCd",
                "cpids[%d].no.cert.ptnCd",
                "cpids[%d].offline.ptnCd"
        };

        boolean flag = false;

        for (String key : keys) {
            if (env.getProperty(String.format(key, index)) != null) {
                flag = true;
            }
        }
        return flag;
    }
    private @NonNull String createTid(String pmCD, String spmCD, MidContext midContext, int day) throws SQLException {
        int sequence = 0;

        while (sequence < 1000) {
            StringBuilder sb = new StringBuilder();
            String mid = midContext.getMid();

            // 전날 날짜 기준 TID 생성
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSSSSS");
            String date = formatter.format(LocalDateTime.now().minusDays(day));

            sb.append(mid);
            sb.append(pmCD);
            sb.append(spmCD);
            sb.append(date, 0, 6);

            // SUBSTR(cur_time, 7, 9) → HHmmssSSS (9자리) 사용
            int timeTail = Integer.parseInt(date.substring(6, 15));

            // LOWER(HEX(CAST(... AS UNSIGNED)))
            String hex = Integer.toHexString(timeTail).toLowerCase();

            sb.append(String.format("%7s", hex).replace(' ', '0'));
            sb.append(String.format("%03d", sequence));


            String tid = sb.toString();

            if (!sessionTids.contains(tid) && !checkExistTID(tid)) {
                sessionTids.add(tid);
                return tid;
            }

            sequence++;
        }

        throw new IllegalStateException("TID 생성 실패 - sequence 초과");
    }
    private boolean checkExistTID(String tid) throws SQLException {
        Connection con = DataSourceUtils.getConnection(dataSource);

        String sql = "SELECT 1 FROM TBTR_MSTR WHERE TID = ? LIMIT 1";
        try(PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, tid);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}