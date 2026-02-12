package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Order(6)
@Component
public class PayProcessStrategy extends AbstractMetaDataProcessStrategy {

    private final TableClassifier classifier;

    public PayProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService, TableClassifier classifier) {
        super(fileReadService, dbProcessService);
        this.classifier = classifier;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isPayRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
        propertiesData.getMidList().forEach(midInitData -> {
            Map<String, String> baseContext = VariableContext.getContextMap(midInitData);

            midInitData.getPayDataList().forEach(payData -> {
                Map<String, String> fullContext = new HashMap<>(baseContext);
                fullContext.putAll(VariableContext.getContextMap(payData, midInitData.getCpidMap().getCertPtnCd()));
                transformAndSave(template, fullContext);
            });
        });
    }
}