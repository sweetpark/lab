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
import java.sql.SQLException;

@Component
@Slf4j
public class TxDataSourceRepo {

    @Autowired
    public DataSource dataSource;

    private Connection con = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    
    public void txDataSourceSave(Member member) throws Exception{

        try{
            con = DataSourceUtils.getConnection(dataSource);
            pstmt = con.prepareStatement("IINSERT INTO MEMBER(name, age, addr) VALUES (?, ?, ?)");
            pstmt.setString(1, member.getName());
            pstmt.setInt(2, member.getAge());
            pstmt.setString(3, member.getAddr());

            pstmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("txDataSourceSave() Error!",e);
        }finally {
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    public void txDataSourceDelete(String name) throws Exception{
        try{
            con = DataSourceUtils.getConnection(dataSource);
            pstmt = con.prepareStatement("DELETE FROM MEMBER WHERE name = ?");
            pstmt.setString(1, name);
            pstmt.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException("txDataSourceDelete Error!", e);
        }finally {
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) DataSourceUtils.releaseConnection(con, dataSource);
        }
    }
}
