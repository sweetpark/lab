package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(100)
@Component
public class DefaultProcessStrategy implements MetaDataProcessStrategy{

    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;

    public DefaultProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService) {
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }


    @Override
    public boolean supports(MetaData metaData) {
        return true;
    }

    @Override
    public void process(MetaData metaData, InitData propertiesData) {
            // data preprocess (파일 read)
            fileReadService.dataPreProcess(metaData);
            // DB Insert
            dbProcessService.save(metaData);
    }
}
