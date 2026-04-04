package com.example.playground.wiezon;

import com.example.playground.wiezon.Enum.oldEnum.Division;
import com.example.playground.wiezon.config.PayBaseProperties;
import com.example.playground.wiezon.context.*;
import com.example.playground.wiezon.context.oldContext.CpidContext;
import com.example.playground.wiezon.context.oldContext.GlobalContext;
import com.example.playground.wiezon.context.oldContext.PayBaseContext;
import com.example.playground.wiezon.context.oldContext.VariableContext;
import com.example.playground.wiezon.service.InitDataAssembler;
import com.example.playground.wiezon.startegy.MetaDataProcessStrategy;
import com.example.playground.wiezon.template.TemplateRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

/**
 * 애플리케이션 실행 시 구동되는 메인 러너 클래스입니다.
 * <p>
 * 1. 초기화 데이터를 조립하고({@link InitDataAssembler}),
 * 2. 리소스 파일(JSON)들을 읽어 파싱한 뒤,
 * 3. 적절한 전략({@link MetaDataProcessStrategy})을 찾아 데이터를 처리합니다.
 * <p>
 * 테스트 환경이 아닐 때(!test) 동작하며, 실행 후 트랜잭션 롤백을 위해 예외를 발생시킵니다.
 */
@Profile("!test")
@Validated
@Component
public class ToolRunner implements ApplicationRunner {


    private final TemplateRegistry templateRegistry;
    private final InitDataAssembler assembler;
    private final List<MetaDataProcessStrategy> strategies;
    private final PayBaseProperties payBaseProperties;
    public static long app_no = 0;


    @Value("${test.mode:FULL}")
    private String mode;



    public ToolRunner(TemplateRegistry templateRegistry, InitDataAssembler assembler, List<MetaDataProcessStrategy> strategies, PayBaseProperties payBaseProperties) {
        this.templateRegistry = templateRegistry;
        this.assembler        = assembler;
        this.strategies       = strategies;
        this.payBaseProperties = payBaseProperties;
    }


    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {

        TemplateRegistry registry = templateRegistry.loadAll();

        GlobalContext globalContext = assembler.getGlobalContext(mode);
        List<CpidContext> cpids = assembler.getBaseCpids();

        if(mode.equalsIgnoreCase("FULL")){
            //(고정) 1. 제휴사 관련 템플릿 (ignore 적용)
            registry.getTemplates(Division.AFFILIATE).forEach(template -> cpids.forEach(cpid -> executeStrategy(template, VariableContext.getContextMap(cpid))));

            // 2. 계약 관련 템플릿
            registry.getTemplates(Division.CONTRACT).forEach(template -> executeStrategy(template, VariableContext.getContextMap(globalContext)));

            //(고정) 3. support 관련 템플릿 (ignore 적용)
            registry.getTemplates(Division.SUPPORT).forEach(template -> executeStrategy(template, Collections.emptyMap()));

            //(고정) 4. gid 관련 템플릿 (ignore 적용)
            registry.getTemplates(Division.GID).forEach(template -> executeStrategy(template, VariableContext.getContextMap(globalContext)));

            //(고정) 5. vid 관련 템플릿 (ignore 적용)
            registry.getTemplates(Division.VID).forEach(template -> executeStrategy(template, VariableContext.getContextMap(globalContext)));

            // 6. mid 관련 템플릿 && pay 관련 템플릿 (pay관련 템플릿은 모듈화)
            assembler.streamMids(globalContext, cpids).forEach(midData -> {

                //2. mid 생성
                registry.getTemplates(Division.MID).forEach(template -> executeStrategy(template, VariableContext.getContextMap(midData)));

                //3. 가맹점 결제 데이터 처리
                assembler.streamPayData(new PayBaseContext(midData)).forEach(payData -> registry.getTemplates(Division.PAY).forEach(template -> executeStrategy(template, VariableContext.getContextMap(payData))));

                assembler.clearTids();
            });
        }else if(mode.equalsIgnoreCase("PAY")){
            assembler.streamPayData(new PayBaseContext(payBaseProperties.getPtnCd(), payBaseProperties.getMid(), payBaseProperties.getGid(), payBaseProperties.getVid())).forEach(payData -> registry.getTemplates(Division.PAY).forEach(template -> executeStrategy(template, VariableContext.getContextMap(payData))));
        }


//        throw new RuntimeException("정상종료 - 강제 트랜잭션 적용");


    }


    private void executeStrategy(TemplateContext template, Map<String, Object> context){
        strategies.stream()
                .filter(s -> s.supports(template))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "처리 가능한 전략이 없습니다. Division: " + template.getDivision()
                ))
                .process(template, context);
    }
}