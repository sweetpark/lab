package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;
import static com.example.playground.wiezon.util.CommonUtil.valueMap;

@Order(3)
@Component
public class GroupProcessStrategy implements MetaDataProcessStrategy{

    private final TableClassifier classifier;
    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;

    public GroupProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        this.classifier = classifier;
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isGidRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
        List<Map<String, Map<String,Object>>> newRows = template.getRows().stream()
                .map( templateRow -> {
                    Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);

                    if(isTemplateMatched(deepRow,"GID", "${GID}")){
                        deepRow.put("GID", valueMap(propertiesData.getMidList().getFirst().getGid()));
                    }

                    if(isTemplateMatched(deepRow, "UID", "${GID}")){
                        deepRow.put("UID", valueMap(propertiesData.getMidList().getFirst().getGid()));
                    }

                    if(isTemplateMatched(deepRow, "CO_NO", "${CO_NO}")){
                        deepRow.put("CO_NO", valueMap(propertiesData.getMidList().getFirst().getCono()));
                    }
                    return deepRow;
                }).toList();

        MetaData newMetaData = new MetaData(template.getTable(), newRows);

        fileReadService.dataPreProcess(newMetaData);
        dbProcessService.save(newMetaData);
    }
}
