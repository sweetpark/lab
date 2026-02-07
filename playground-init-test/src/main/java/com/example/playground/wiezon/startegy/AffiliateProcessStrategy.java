package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class AffiliateProcessStrategy extends AbstractMetaDataProcessStrategy {

    private final TableClassifier classifier;

    public AffiliateProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService, TableClassifier classifier) {
        super(fileReadService, dbProcessService);
        this.classifier = classifier;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isAffiliate(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
        for (CpidMap cpidMap : propertiesData.getCpidList()) {
            transformAndSave(template, VariableContext.getContextMap(cpidMap));
        }
    }
}