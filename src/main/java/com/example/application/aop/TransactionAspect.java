package com.example.application.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Aspect
public class TransactionAspect {

    private final PlatformTransactionManager transactionManager;

    public TransactionAspect(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    @Around("@annotation(CustomTransaction)")
    public Object around(ProceedingJoinPoint joinPoint){



        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName(joinPoint.getSignature().toShortString());
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try{
            Object proceed = joinPoint.proceed();
            transactionManager.commit(status);
            return proceed;
        }catch (Throwable e){

            transactionManager.rollback(status);

            Map<String, Object> result =  new HashMap<>();
            log.error("[Error] {}", e.getMessage());
            result.put("code", "404");
            return result;
        }


    }

}
