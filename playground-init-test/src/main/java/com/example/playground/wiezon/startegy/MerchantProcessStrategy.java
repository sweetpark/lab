package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.MidInitData;
import com.example.playground.wiezon.dto.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(5)
@Component
public class MerchantProcessStrategy extends AbstractMetaDataProcessStrategy {

    private final TableClassifier classifier;

    public MerchantProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        super(fileReadService, dbProcessService);
        this.classifier = classifier;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isMidRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
        for (MidInitData midInitData : propertiesData.getMidList()) {
            transformAndSave(template, VariableContext.getContextMap(midInitData));
        }
    }
}