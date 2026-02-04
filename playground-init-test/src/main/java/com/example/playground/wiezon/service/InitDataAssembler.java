package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MidInitData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class InitDataAssembler {

    @Autowired
    DataSource dataSource;
    @Autowired
    Environment environment;

    public InitData assemble(){
        InitData initData = new InitData();

        //mid 하나당, cpid 한 종류
        int index = 0;
        while(true){
            if(environment.getProperty(String.format("cpids[%d].ptnCd", index)) == null) break;

            MidInitData midInitData = new MidInitData();
            midInitData.setMid(String.format(environment.getProperty("mid") + "%03d" +'m',index));
            midInitData.setCono(getCoNo(dataSource));


            CpidMap cpidMap = new CpidMap();

            //인증
            cpidMap.setCertPtnCd(prop("cpids[%d].cert.ptnCd", index));
            cpidMap.setCertCpid(prop( "cpids[%d].cert.cpid", index));
            cpidMap.setCertKeyType(prop( "cpids[%d].cert.keyType", index));
            cpidMap.setCertKey(prop( "cpids[%d].cert.key", index));

            //구인증
            cpidMap.setOldCertPtnCd(prop("cpids[%d].old.cert.ptnCd", index));
            cpidMap.setOldCertCpid(prop( "cpids[%d].old.cert.cpid", index));
            cpidMap.setOldCertKeyType(prop( "cpids[%d].old.cert.keyType", index));
            cpidMap.setOldCertKey(prop( "cpids[%d].old.cert.key", index));

            //비인증
            cpidMap.setNoCertPtnCd(prop( "cpids[%d].no.cert.ptnCd", index));
            cpidMap.setNoCertCpid(prop( "cpids[%d].no.cert.cpid", index));
            cpidMap.setNoCertKeyType(prop( "cpids[%d].no.cert.keyType", index));
            cpidMap.setNoCertKey(prop( "cpids[%d].no.cert.key", index));

            //오프라인
            cpidMap.setOfflinePtnCd(prop( "cpids[%d].offline.ptnCd", index));
            cpidMap.setOfflineCpid(prop( "cpids[%d].offline.cpid", index));
            cpidMap.setOfflineKeyType(prop( "cpids[%d].offline.keyType", index));
            cpidMap.setOfflineKey(prop( "cpids[%d].offline.key", index));

            midInitData.setCpidMap(cpidMap);
            initData.addCpidList(cpidMap);

            initData.addMidList(midInitData);
            index++;
        }

        return initData;
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
    private String prop(String key, int index){
        return environment.getProperty(String.format(key, index));
    }
}
