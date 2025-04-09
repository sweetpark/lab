package com.example.application.cache;

import com.example.application.cache.entity.Member;
import com.example.application.cache.entity.Terms;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final Repository repository;

    @Cacheable(value = "member", key = "#name")
    public Member getMember(String name){
        return repository.getMember(name);
    }

    @Cacheable(value = "terms", key = "#name")
    public Terms getTerms(String name){
        return repository.getTerms(name);
    }

    @CachePut(value = "member", key = "#member.name")
    public Member updateMember(Member member){
        Member oldMember = repository.getMember(member.getName());
        if( oldMember != null ) {
            oldMember.setAddr(member.getAddr());
            oldMember.setAge(member.getAge());
        }

        return oldMember;
    }

    @CachePut(value = "terms", key = "#terms.name")
    public Terms updateTerms(Terms terms){
        Terms oldTerms = repository.getTerms(terms.getTermsName());
        if( oldTerms != null ){
            oldTerms.setAgreed(terms.getAgreed());
            oldTerms.setUpdateTime(terms.getUpdateTime());
        }

        return oldTerms;
    }

    @CacheEvict(value = "member", key = "#name", beforeInvocation = true)
    public void removeMember(String name){
        repository.removeMember(name);
    }


    //전체 캐시 비우기
    @CacheEvict(value = "member", allEntries = true)
    public void clearAllMembers(){}
}
