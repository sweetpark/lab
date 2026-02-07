package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.VariableContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.util.DataVariableResolver;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;

@Order(4)
@Component
public class VirtualProcessStrategy implements MetaDataProcessStrategy{

    private final TableClassifier classifier;
    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;

    public VirtualProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        this.classifier = classifier;
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isVidRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
        Map<String, String> variables = VariableContext.getContextMap(propertiesData.getMidList().getFirst());

        List<Map<String, Map<String,Object>>> newRows = template.getRows().stream()
                .map( templateRow -> {
                    Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);
                    DataVariableResolver.replace(deepRow, variables);
                    return deepRow;
                }).toList();

        MetaData newMetaData = new MetaData(template.getTable(), newRows);

        fileReadService.dataPreProcess(newMetaData);
        dbProcessService.save(newMetaData);
    }
}
