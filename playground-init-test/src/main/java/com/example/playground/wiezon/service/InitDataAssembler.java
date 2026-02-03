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
            cpidMap.setPtnCd(environment.getProperty(String.format("cpids[%d].ptnCd", index)));
            cpidMap.setKeyType(environment.getProperty(String.format("cpids[%d].keyType", index)));
            cpidMap.setKey(environment.getProperty(String.format("cpids[%d].key", index)));

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
}
