package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.MidInitData;
import com.example.playground.wiezon.dto.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.util.DataVariableResolver;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;

@Order(5)
@Component
public class MerchantProcessStrategy implements MetaDataProcessStrategy{

    private final TableClassifier classifier;
    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;

    public MerchantProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        this.classifier = classifier;
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }


    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isMidRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
            merchantProcess(template,propertiesData).forEach(
                    processed -> {
                        fileReadService.dataPreProcess(processed);
                        dbProcessService.save(processed);
                    }
            );
    }


    private List<MetaData> merchantProcess(MetaData template, InitData propertiesData){
        List<MetaData> resultList = new ArrayList<>();

        for(MidInitData midInitData :propertiesData.getMidList()){
            Map<String, String> variables = VariableContext.getContextMap(midInitData);

            List<Map<String, Map<String,Object>>> newRows = template.getRows().stream()
                    .map( templateRow -> {
                        Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);

                        // 모든 컬럼에 대해 ${} 패턴 자동 치환
                        DataVariableResolver.replace(deepRow, variables);

                        return deepRow;
                    }).toList();

            resultList.add(new MetaData(template.getTable(), newRows));
        }

        return resultList;
    }
}
