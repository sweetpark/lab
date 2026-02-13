package com.example.playground.wiezon;

import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.service.InitDataAssembler;
import com.example.playground.wiezon.startegy.MetaDataProcessStrategy;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
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
@Component
public class ToolRunner implements ApplicationRunner {


    private final ResourcePatternResolver resolver;
    private final FileReadService fileReadService;
    private final InitDataAssembler assembler;
    private final List<MetaDataProcessStrategy> strategies;
    public static long app_no = 0;

    public ToolRunner(ResourcePatternResolver resolver, FileReadService fileReadService, InitDataAssembler assembler, List<MetaDataProcessStrategy> strategies) {
        this.resolver = resolver;
        this.fileReadService = fileReadService;
        this.assembler = assembler;
        this.strategies = strategies;
    }


    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {


        Resource[] resources = resolver.getResources("classpath:/data/**/*.json");

        // 0. properties load
        InitData propertiesData = assembler.assemble();

        Arrays.stream(resources).forEach(resource -> {

            try(InputStream is = resource.getInputStream()){

                // 1. file read
                MetaData metaData = fileReadService.parseJson(is);
                // 2. 패턴별 실행
                strategies.stream()
                        .filter(s -> s.supports(metaData))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException("처리 패턴이 없습니다.")
                        )
                        .process(metaData, propertiesData);


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }
}