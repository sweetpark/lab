package com.example.playground.jdbc.source.reposiory;

import com.example.playground.jdbc.source.Member;
import com.example.playground.jdbc.source.dbUtil.DBConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MemberRepository {


    private Connection con = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;


    public void save(Member member) throws SQLException{
        try{
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement("INSERT INTO MEMBER(name, age, addr) VALUES (?,?,?)");
            pstmt.setString(1, member.getName());
            pstmt.setInt(2, member.getAge());
            pstmt.setString(3, member.getAddr());
            pstmt.executeUpdate();
        }catch (SQLException e){
            log.error("[JDBC] save() error {}",e.getMessage());
            throw new RuntimeException("save() error!", e);
        }finally{
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) con.close();
        }

    }


    public void update(Member newMember, Member oldMember) throws  SQLException{
        try{
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement("UPDATE MEMBER SET name = ?, age = ?, addr = ? WHERE name = ?");
            pstmt.setString(1, newMember.getName());
            pstmt.setInt(2, newMember.getAge());
            pstmt.setString(3, newMember.getAddr());
            pstmt.setString(4, oldMember.getName());
            pstmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Update Error !", e);
        }finally{
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) con.close();
        }

    }


    public Optional<Member> findByName(String name) throws SQLException{
        try{
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement("SELECT name, age, addr FROM MEMBER WHERE name = ?");
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setName(rs.getString("name"));
                member.setAge(rs.getInt("age"));
                member.setAddr(rs.getString("addr"));
                return Optional.of(member);
            }
            return Optional.empty();
        }catch(SQLException e){
            throw new RuntimeException("findById Error ! ", e);
        }finally {
            if(rs != null )rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null ) con.close();
        }

    }


    public List<Member> findAll() throws SQLException{
        try{
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement("SELECT name, age, addr FROM MEMBER");
            rs = pstmt.executeQuery();
            List<Member> result = new ArrayList<>();
            while(rs.next()){
                Member member = new Member();
                member.setName(rs.getString("name"));
                member.setAge(rs.getInt("age"));
                member.setAddr(rs.getString("age"));
                result.add(member);
            }

            return result;
        }catch(SQLException e){
            throw new RuntimeException("findAll() Error!");
        }finally {
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con!= null )con.close();
        }

    }

    public void delete(String name) throws SQLException{
        try{
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement("DELETE FROM MEMBER WHERE name = ?");
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("DELETE ERRPR!", e);
        }finally {
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(con != null) con.close();
        }
    }
}
