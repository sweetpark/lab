package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 특정 분류에 속하지 않는 기타 데이터를 처리하는 기본 전략 클래스입니다.
 * <p>
 * 별도의 변수 치환 없이 템플릿 그대로 전처리 및 저장을 수행합니다.
 */
@Order(1)
@Component
public class BasicProcessStrategy extends AbstractMetaDataProcessStrategy{

    private final TableClassifier tableClassifier;

    public BasicProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService, TableClassifier tableClassifier) {
        super(fileReadService, dbProcessService);
        this.tableClassifier = tableClassifier;
    }

    /**
     * 일반적인 전략
     */
    @Override
    public boolean supports(TemplateContext templateContext) {
        return tableClassifier.isBasicRelated(templateContext);
    }

    @Override
    public void process(TemplateContext template, Map<String, Object> context) {
        transformAndSave(template, context);
    }
}