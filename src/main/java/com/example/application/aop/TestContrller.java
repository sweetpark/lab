package com.example.application.aop;

import com.example.application.aop.selfCall.Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class TestContrller {

    @Autowired
    private ApplicationContext ctx;

    @PostMapping("/selfCall")
    public String test(@RequestBody Map<String, Object> param){

        try{
            Handler handler = ctx.getBean(param.get("command").toString(),Handler.class);
            Object run = handler.run();

            return "success";

        }catch(Exception e){
            log.error("테스트 실패");
            return "fail";
        }

    }
}
