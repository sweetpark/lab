package com.example.playground.cache.src;

import com.example.playground.cache.src.entity.Member;
import com.example.playground.cache.src.entity.Terms;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Repository {
    public final static Map<String, Object> store = new ConcurrentHashMap<>();

    public void setMember(Member member){
        store.putIfAbsent(member.getName(), member);
    }

    public void setTerms(Terms terms){
        store.putIfAbsent(terms.getTermsName(), terms);
    }

    public Member getMember(String name){
        return (Member)store.get(name);
    }

    public Terms getTerms(String name){
        return (Terms) store.get(name);
    }

    public void removeMember(String name){
        store.remove(name);
    }



}
