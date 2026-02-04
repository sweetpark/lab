package com.example.playground.wiezon;

import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.service.InitDataAssembler;
import com.example.playground.wiezon.startegy.MetaDataProcessStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

@Profile("!test")
@Component
public class ToolRunner implements ApplicationRunner {


    private final ResourcePatternResolver resolver;
    private final FileReadService fileReadService;
    private final InitDataAssembler assembler;
    private final List<MetaDataProcessStrategy> strategies;

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

        Arrays.stream(resources).forEach(new Consumer<Resource>() {
            @Override
            public void accept(Resource resource) {
                try(InputStream is = resource.getInputStream()){
                    // 0. properties load
                    InitData propertiesData = assembler.assemble();

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


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }
}
