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