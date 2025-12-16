package com.example.playground.jdbc.source.template;

import com.example.playground.jdbc.source.Member;
import com.example.playground.jdbc.source.reposiory.TxTemplateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Map;

@Service
public class TxTemplateService {

    @Configuration
    public static class Config{
        @Autowired
        DataSource dataSource;

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
            return new TransactionTemplate(transactionManager);
        }
    }

    @Autowired
    TxTemplateRepo txTemplateRepo;
    @Autowired
    TransactionTemplate template;

    public void txTemplateSaveAfterDelete(Map<String, Object> params)throws Exception{

        template.executeWithoutResult((status) ->{
            try{
                Member member = new Member();
                member.setName(params.get("name").toString());
                member.setAge(Integer.parseInt(params.get("age").toString()));
                member.setAddr(params.get("addr").toString());
                txTemplateRepo.delete(params.get("oldName").toString());
                txTemplateRepo.save(member);

            }catch (Exception e) {
                throw new RuntimeException("txDataSourceSaveAfterDelete Service Error!", e);
            }
        });
    }
}
