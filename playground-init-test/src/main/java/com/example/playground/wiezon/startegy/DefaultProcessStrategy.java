package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.context.InitData;
import com.example.playground.wiezon.context.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 특정 분류에 속하지 않는 기타 데이터를 처리하는 기본 전략 클래스입니다.
 * <p>
 * 별도의 변수 치환 없이 템플릿 그대로 전처리 및 저장을 수행합니다.
 */
@Order(100)
@Component
public class DefaultProcessStrategy implements MetaDataProcessStrategy{

    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;

    public DefaultProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService) {
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }

    /**
     * 다른 전략들이 처리하지 않은 모든 메타데이터를 지원합니다. (항상 true)
     */
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