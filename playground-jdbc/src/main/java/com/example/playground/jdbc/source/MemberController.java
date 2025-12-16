package com.example.playground.jdbc.source;

import com.example.playground.jdbc.source.reposiory.JDBCTemplateRepo;
import com.example.playground.jdbc.source.reposiory.MemberRepository;
import com.example.playground.jdbc.source.template.TxTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class MemberController {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    TxTemplateService txTemplateService;

    @Autowired
    JDBCTemplateRepo jdbcTemplateRepo;

    @PostMapping("/member/save")
    public String memberSave(@RequestBody Map<String ,Object> param){

        try{
            Member member = new Member();

            member.setName(param.get("name").toString());
            member.setAge(Integer.parseInt(param.get("age").toString()));
            member.setAddr(param.get("addr").toString());

            log.info("[PARAMS] => {}", param);
            memberRepository.save(member);
        }catch(SQLException e) {
            throw new RuntimeException("SQL Exception close Error!", e);
        }finally{
            return "success";
        }
    }

    @PostMapping("/member/update")
    public String update(@RequestBody Map<String, Object> params) throws Exception{
        String oldName = params.get("oldName").toString();
        Optional<Member> member = memberRepository.findByName(oldName);
        if(member.isPresent()){
            Member oldMember = member.get();
            Member newMember = new Member();
            newMember.setName(params.get("name").toString());
            newMember.setAge(Integer.parseInt(params.get("age").toString()));
            newMember.setAddr(params.get("addr").toString());
            memberRepository.update(newMember, oldMember);
        }
        return "success";
    }

    @PostMapping("/member/delete")
    public String all(@RequestBody Map<String, Object> params) throws Exception{
        String name = params.get("name").toString();
        memberRepository.delete(name);
        return "success";
    }

    @PostMapping("/transaction")
    public String transaction(@RequestBody Map<String, Object> params) throws Exception{
        memberService.txSaveAfterDelete(params);
        return "success";
    }

    @PostMapping("/txDataSource")
    public String txDataSource(@RequestBody Map<String, Object> params) throws Exception{
        memberService.txDataSourceSaveAfterDelete(params);
        return "success";
    }

    @PostMapping("/txTemplate")
    public String txTemplate(@RequestBody Map<String, Object> params) throws Exception{
        txTemplateService.txTemplateSaveAfterDelete(params);
        return "success";
    }

    @PostMapping("/member/all")
    public String all() throws  Exception{
        return memberRepository.findAll().toString();
    }


    @GetMapping("/template/all")
    public String templateAll() throws Exception{
        return jdbcTemplateRepo.findAll().toString();
    }

    @PostMapping("/template/save")
    public String templateSave(@RequestBody Map<String, Object> params)  throws Exception{
        Member member= new Member();
        member.setName(params.get("name").toString());
        member.setAge(Integer.parseInt(params.get("age").toString()));
        member.setAddr(params.get("addr").toString());
        jdbcTemplateRepo.save(member);

        return "success";
    }

}
