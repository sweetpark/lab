package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 그룹(GID) 관련 데이터를 처리하는 전략 클래스입니다.
 */
@Order(2)
@Component
public class SupportProcessStrategy extends AbstractMetaDataProcessStrategy {

    private final TableClassifier classifier;

    public SupportProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        super(fileReadService, dbProcessService);
        this.classifier = classifier;
    }

    @Override
    public boolean supports(TemplateContext templateContext) {
        return classifier.isSupportRelated(templateContext);
    }

    @Override
    public void process(TemplateContext template, Map<String, Object> context) {
        // data preprocess (파일 read)
        fileReadService.dataPreProcess(template);
        // DB Insert
        dbProcessService.save(template);
    }
}
