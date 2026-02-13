package com.example.playground.wiezon.service;

import com.example.playground.wiezon.ToolRunner;
import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MidInitData;
import com.example.playground.wiezon.dto.PayData;
import com.example.playground.wiezon._enum.PaymentDetailType;
import com.example.playground.wiezon._enum.PaymentMethod;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    /**
     * 초기화 데이터를 수집 및 조립합니다.
     *
     * @return 조립된 {@link InitData} 객체
     */
    public InitData assemble() throws SQLException {
        sessionTids.clear();
        String co_no = getCoNo(dataSource);
        InitData initData = new InitData();
        //mid 하나당, cpid 한 종류
        int index = 0;
        while (existsCpidsIndex(environment, index)) {

            MidInitData midInitData = new MidInitData();
            midInitData.setMid(String.format(environment.getProperty("mid") + "%03d" + 'm', index));
            midInitData.setCono(co_no);
            midInitData.setGid(environment.getProperty("gid"));
            midInitData.setL1Vid(environment.getProperty("l1_vid"));
            midInitData.setCrctPtnCd(environment.getProperty("crct.ptnCd"));
            midInitData.setCrctCpid(environment.getProperty("crct.cpid"));
            midInitData.setCrctKeyType(environment.getProperty("crct.keyType"));
            midInitData.setCrctKey(environment.getProperty("crct.key"));


            CpidMap cpidMap = new CpidMap();

            //인증
            cpidMap.setCertPtnCd(prop("cpids[%d].cert.ptnCd", index));
            cpidMap.setCertCpid(prop("cpids[%d].cert.cpid", index));
            cpidMap.setCertKeyType(prop("cpids[%d].cert.keyType", index));
            cpidMap.setCertKey(prop("cpids[%d].cert.key", index));

            //구인증
            cpidMap.setOldCertPtnCd(prop("cpids[%d].old.cert.ptnCd", index));
            cpidMap.setOldCertCpid(prop("cpids[%d].old.cert.cpid", index));
            cpidMap.setOldCertKeyType(prop("cpids[%d].old.cert.keyType", index));
            cpidMap.setOldCertKey(prop("cpids[%d].old.cert.key", index));

            //비인증
            cpidMap.setNoCertPtnCd(prop("cpids[%d].no.cert.ptnCd", index));
            cpidMap.setNoCertCpid(prop("cpids[%d].no.cert.cpid", index));
            cpidMap.setNoCertKeyType(prop("cpids[%d].no.cert.keyType", index));
            cpidMap.setNoCertKey(prop("cpids[%d].no.cert.key", index));

            //오프라인
            cpidMap.setOfflinePtnCd(prop("cpids[%d].offline.ptnCd", index));
            cpidMap.setOfflineCpid(prop("cpids[%d].offline.cpid", index));
            cpidMap.setOfflineKeyType(prop("cpids[%d].offline.keyType", index));
            cpidMap.setOfflineKey(prop("cpids[%d].offline.key", index));

            midInitData.setCpidMap(cpidMap);
            midInitData.setPayDataList(createPayData(midInitData));
            initData.addCpidList(cpidMap);

            initData.addMidList(midInitData);
            index++;
        }

        return initData;
    }


    private String getCoNo(DataSource dataSource) {

        Connection con = DataSourceUtils.getConnection(dataSource);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = LocalDateTime.now().format(formatter);

        String sql =
                "SELECT tmma.CO_NO " +
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
    private List<PayData> createPayData(MidInitData midInitData) throws SQLException {

        List<PayData> result = new ArrayList<>();

        //FIXME) 결제 날짜 지정 필요

        // 오늘
        int day = 0;
        // 100개 거래 내역 정보 저장
        for(int i = 0; i < 100; i ++){
            if(i > 50) day = 1;
            PayData payData = new PayData();
            payData.setPmCd(PaymentMethod.CREDIT_CARD.getCode());
            payData.setSpmCd(PaymentDetailType.CARD_AUTH.getDetailCode());
            payData.setTid1(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midInitData, day));
            payData.setTid2(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midInitData, day));
            payData.setTid1P1(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midInitData, day));
            payData.setTid1P2(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midInitData, day));
            payData.setTid1P3(createTid(PaymentMethod.CREDIT_CARD.getCode(), PaymentDetailType.CARD_AUTH.getDetailCode(), midInitData, day));
            payData.setAppNo1(String.format("%08d",ToolRunner.app_no++));
            payData.setAppNo2(String.format("%08d",ToolRunner.app_no++));
            payData.setAppNo3(String.format("%08d",ToolRunner.app_no++));

            result.add(payData);
        }

        return result;
    }
    private @NonNull String createTid(String pmCD, String spmCD, MidInitData midInitData, int day) throws SQLException {
        int sequence = 0;

        while (sequence < 1000) {
            StringBuilder sb = new StringBuilder();
            String mid = midInitData.getMid();

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