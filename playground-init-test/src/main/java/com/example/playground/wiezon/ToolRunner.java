package com.example.playground.wiezon;

import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.util.CryptoUtil;
import kms.wiezon.com.crypt.CryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;


@Component
public class ToolRunner implements ApplicationRunner {



    @Autowired
    private ResourcePatternResolver resolver;

    @Autowired
    private FileReadService fileReadService;
    @Autowired
    private DBProcessService dbProcessService;
    private final CryptUtils cryptUtils = CryptoUtil.getCryptoUtils();



    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {


        Resource[] resources = resolver.getResources("classpath:/data/**/*.json");

        Arrays.stream(resources).forEach(new Consumer<Resource>() {
            @Override
            public void accept(Resource resource) {
                try(InputStream is = resource.getInputStream()){

                    // 1. file read
                    MetaData metaData = fileReadService.parseJson(is);

                    // 2. data preprocess
                    fileReadService.dataPreProcess(metaData);

                    // 3. DB Insert
                    dbProcessService.save(metaData);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }
}
