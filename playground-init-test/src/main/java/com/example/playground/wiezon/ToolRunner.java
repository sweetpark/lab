package com.example.playground.wiezon;

import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.DataProcService;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.service.InitDataAssembler;
import com.example.playground.wiezon.util.CryptoUtil;
import kms.wiezon.com.crypt.CryptUtils;
import org.bouncycastle.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

@Profile("!test")
@Component
public class ToolRunner implements ApplicationRunner {



    @Autowired
    private ResourcePatternResolver resolver;

    @Autowired
    private DataProcService dataProcService;
    @Autowired
    private FileReadService fileReadService;
    @Autowired
    private DBProcessService dbProcessService;
    @Autowired
    private InitDataAssembler assembler;

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

                    // 2. data preprocess (제휴사)
                    dataProcService.affiliateProcess(metaData, propertiesData).forEach(
                            processedMetaData ->
                            {
                                // data preprocess (파일 read)
                                fileReadService.dataPreProcess(processedMetaData);
                                // DB Insert
                                dbProcessService.save(processedMetaData);
                            }
                    );


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }
}
