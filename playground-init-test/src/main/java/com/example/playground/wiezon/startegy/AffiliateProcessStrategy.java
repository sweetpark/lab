package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.util.DataVariableResolver;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;

@Order(1)
@Component
public class AffiliateProcessStrategy implements MetaDataProcessStrategy{

    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;
    private final TableClassifier classifier;

    public AffiliateProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService, TableClassifier classifier) {
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
        this.classifier = classifier;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isAffiliate(metaData);
    }

    // 제휴사 Insert 로직
    @Override
    public void process(MetaData template, InitData propertiesData) {
        affiliateProcess(template, propertiesData)
                .forEach(processed -> {
                    fileReadService.dataPreProcess(processed);
                    dbProcessService.save(processed);
                });
    }


    private List<MetaData> affiliateProcess(MetaData template, InitData propertiesData){

        List<MetaData> resultList = new ArrayList<>();

        for(CpidMap cpidMap : propertiesData.getCpidList() ){
            Map<String, String> variables = VariableContext.getContextMap(cpidMap);

            // template 에서 해당 값 바꿔치기
            List<Map<String, Map<String, Object>>> newRows = template.getRows().stream()
                    .map(templateRow -> {
                        Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);
                        DataVariableResolver.replace(deepRow, variables);
                        return deepRow;
                    })
                    .collect(Collectors.toList());


            resultList.add(new MetaData(template.getTable(), newRows));
        }


        return resultList;
    }
}
