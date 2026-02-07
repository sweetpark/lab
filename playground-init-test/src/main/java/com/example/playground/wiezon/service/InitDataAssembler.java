package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MidInitData;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 초기화에 필요한 기본 데이터(InitData)를 조립하는 서비스입니다.
 * <p>
 * Environment 프로퍼티와 DB 조회를 통해 MID, CPID 설정 등의 데이터를 수집하여 {@link InitData} 객체를 생성합니다.
 */
@Service
public class InitDataAssembler {


    private final DataSource dataSource;
    private final Environment environment;

    public InitDataAssembler(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }


    /**
     * 초기화 데이터를 수집 및 조립합니다.
     *
     * @return 조립된 {@link InitData} 객체
     */
    public InitData assemble(){
        InitData initData = new InitData();

        //mid 하나당, cpid 한 종류
        int index = 0;
        while (existsCpidsIndex(environment, index)) {

            MidInitData midInitData = new MidInitData();
            midInitData.setMid(String.format(environment.getProperty("mid") + "%03d" + 'm', index));
            midInitData.setCono(getCoNo(dataSource));
            midInitData.setGid(environment.getProperty("gid"));
            midInitData.setL1Vid(environment.getProperty("l1_vid"));


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
            initData.addCpidList(cpidMap);

            initData.addMidList(midInitData);
            index++;
        }

        return initData;
    }

    private String getCoNo(DataSource dataSource) {

        Connection con = DataSourceUtils.getConnection(dataSource);

        String sql =
                "SELECT tmma.CO_NO " +
                        "FROM TBSI_MS_MBS_ALL tmma " +
                        "LEFT JOIN TBSI_CO tsc ON tmma.CO_NO = tsc.CO_NO " +
                        "WHERE tmma.SM_MBS_CD = ? " +
                        "AND tsc.CO_NO IS NULL " +
                        "ORDER BY tmma.TO_DT DESC " +
                        "LIMIT 1";

        try(PreparedStatement pstmt = con.prepareStatement(sql)) {

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
}