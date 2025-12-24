package com.example.playground.jdbc.source.reposiory;

import com.example.playground.jdbc.source.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class TXMemberRepository {

    private PreparedStatement pstmt = null;
    private ResultSet rs = null;


    public void txSave(Connection con, Member member) throws Exception{
        try{
            // 일부로 rollback
            pstmt = con.prepareStatement("IINSERT INTO MEMBER (name, age, addr) VALUES (?, ?, ?)");
            pstmt.setString(1, member.getName());
            pstmt.setInt(2, member.getAge());
            pstmt.setString(3, member.getAddr());
            pstmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("txSave() Error!", e);
        }finally{
            if(pstmt != null) pstmt.close();
            if(rs != null) rs.close();
        }
    }


    public void txDelete(Connection con, String name) throws Exception{
        try{
            pstmt = con.prepareStatement("DELETE FROM MEMBER WHERE name = ?");
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("txDelete Error!", e);
        }finally{
            if(pstmt != null) pstmt.close();
            if(rs != null) rs.close();
        }
    }

}
