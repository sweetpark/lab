package com.example.playground.wiezon;

import com.example.playground.wiezon.service.FileReadService;
import kms.wiezon.com.crypt.CryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ToolRunner implements ApplicationRunner {

    @Autowired
    private CryptUtils cryptUtils;

    @Autowired
    private FileReadService fileReadService;

    

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Path> result;
        try (Stream<Path> walk = Files.walk(Path.of("data"))) {
            result = walk.toList();

            System.out.println(cryptUtils.encrypt("data"));

            for(Path path : result){
                fileReadService.parseJson(path);
            }
        }



    }
}
