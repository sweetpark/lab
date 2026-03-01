package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.context.InitData;
import com.example.playground.wiezon.context.MetaData;
import com.example.playground.wiezon.context.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 그룹(GID) 관련 데이터를 처리하는 전략 클래스입니다.
 */
@Order(3)
@Component
public class GroupProcessStrategy extends AbstractMetaDataProcessStrategy {

    private final TableClassifier classifier;

    public GroupProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        super(fileReadService, dbProcessService);
        this.classifier = classifier;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isGidRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
        transformAndSave(template, VariableContext.getContextMap(propertiesData.getMidList().getFirst()));
    }
}
