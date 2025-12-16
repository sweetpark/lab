package com.example.playground.jdbc.source.reposiory;


import com.example.playground.jdbc.source.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
@Slf4j
public class TxTemplateRepo {

    @Autowired
    DataSource dataSource;

    private Connection con = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    public void save(Member member) throws Exception {
        try{
            // con = dataSource.getConnection(); 직접 호출하면 동일한 connection이 연결되는게 아님 -> DataSourceUtils를 이용해야함
            con = DataSourceUtils.getConnection(dataSource);
            pstmt = con.prepareStatement("IINSERT INTO MEMBER(name, age, addr) VALUES (?, ?, ?)");
            pstmt.setString(1, member.getName());
            pstmt.setInt(2,member.getAge());
            pstmt.setString(3, member.getAddr());
            pstmt.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException("txTemplate save() error!", e);
        }finally {
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public void delete(String name) throws  Exception{
        try{
            con = DataSourceUtils.getConnection(dataSource);
            pstmt = con.prepareStatement("DELETE FROM MEMBER WHERE name = ?");
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException("txTemplate delete error!", e);
        }finally {
            if(rs != null )rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) DataSourceUtils.releaseConnection(con,dataSource);
        }
    }
}
