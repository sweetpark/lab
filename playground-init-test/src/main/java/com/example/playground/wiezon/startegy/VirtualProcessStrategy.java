package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 가상계좌/가상그룹(VID) 관련 데이터를 처리하는 전략 클래스입니다.
 */
@Order(4)
@Component
public class VirtualProcessStrategy extends AbstractMetaDataProcessStrategy {

    private final TableClassifier classifier;

    public VirtualProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        super(fileReadService, dbProcessService);
        this.classifier = classifier;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isVidRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
        transformAndSave(template, VariableContext.getContextMap(propertiesData.getMidList().getFirst()));
    }
}
