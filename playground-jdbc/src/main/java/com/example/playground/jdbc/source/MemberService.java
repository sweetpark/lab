package com.example.playground.jdbc.source;

import com.example.playground.jdbc.source.dbUtil.DBConnectionUtil;
import com.example.playground.jdbc.source.reposiory.MemberRepository;
import com.example.playground.jdbc.source.reposiory.TXMemberRepository;
import com.example.playground.jdbc.source.reposiory.TxDataSourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.util.Map;

@Service
public class MemberService {

    //Connection 파라미터 관리
    @Autowired
    TXMemberRepository txMemberRepository;
    @Autowired
    MemberRepository memberRepository;

    //DataSource
    @Autowired
    TxDataSourceRepo txDataSourceRepo;
    @Autowired
    PlatformTransactionManager transactionManager;

    //Connection 파라미터
    public void txSaveAfterDelete(Map<String, Object> params) throws Exception{
        Connection con = DBConnectionUtil.getConnection();
        con.setAutoCommit(false);
        try{
            Member oldMember =  memberRepository.findByName(params.get("oldName").toString()).orElseThrow(
                    () ->  new RuntimeException("no find member")
            );

            Member uptMember = new Member();
            uptMember.setName(params.get("name").toString());
            uptMember.setAge(Integer.parseInt(params.get("age").toString()));
            uptMember.setAddr(params.get("addr").toString());

            // 1. delete 후 save()
            txMemberRepository.txDelete(con,oldMember.getName());
            txMemberRepository.txSave(con, uptMember);
            con.commit();
        }catch(Exception e){
            con.rollback();
            throw new RuntimeException("[TRANSACTION] 실패로 인한 rollback");
        }finally {
            con.setAutoCommit(true);
            if (con != null) con.close();
        }
    }

    // DataSource tx관리
    public void txDataSourceSaveAfterDelete(Map<String, Object>params)throws Exception{
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            Member member = new Member();
            member.setName(params.get("name").toString());
            member.setAge(Integer.parseInt(params.get("age").toString()));
            member.setAddr(params.get("addr").toString());
            txDataSourceRepo.txDataSourceDelete(params.get("oldName").toString());
            txDataSourceRepo.txDataSourceSave(member);

            transactionManager.commit(status);
        }catch (Exception e) {
            transactionManager.rollback(status);
            throw new RuntimeException("txDataSourceSaveAfterDelete Service Error!", e);
        }

    }
}
