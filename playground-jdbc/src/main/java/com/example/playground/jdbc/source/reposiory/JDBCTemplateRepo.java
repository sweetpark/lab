package com.example.playground.jdbc.source.reposiory;

import com.example.playground.jdbc.source.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JDBCTemplateRepo {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void namedSave(Member member){
        Map<String, Object> param = new HashMap<>();
        param.put("name", member.getName());
        param.put("age", member.getAge());
        param.put("addr", member.getAddr());
        namedParameterJdbcTemplate.update("INSERT INTO MEMBER(name, age, addr) VALUES (:name, :age, :addr)",
                param);
    }

    public void save(Member member){
        jdbcTemplate.update("INSERT INTO MEMBER(name, age, addr) VALUES (?, ?, ?) ",
                member.getName(),
                member.getAge(),
                member.getAddr()
        );
    }

    public List<Member> findAll(){
        return jdbcTemplate.query("SELECT * FROM MEMBER", (rs, rowNum) -> {
            Member member = new Member();
            member.setName(rs.getString("name"));
            member.setAge(rs.getInt("age"));
            member.setAddr(rs.getString("addr"));
            return member;
        });
    }

    public Member findByName(String name){
        return jdbcTemplate.queryForObject("SELECT * FROM MEMBER WHERE name = ?", (rs, rowNum) -> {
            Member member = new Member();
            member.setName(rs.getString("name"));
            member.setAge(rs.getInt("age"));
            member.setAddr(rs.getString("addr"));
            return member;
        }, name);
    }

    public void delete(String name){
        jdbcTemplate.update("DELETE FROM MEMBER WHERE name = ?", name);
    }
}
