package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.util.DataVariableResolver;

import java.util.List;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;

public abstract class AbstractMetaDataProcessStrategy implements MetaDataProcessStrategy {

    protected final FileReadService fileReadService;
    protected final DBProcessService dbProcessService;

    protected AbstractMetaDataProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService) {
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }

    /**
     * 템플릿과 변수 맵을 받아 데이터를 변환한 후 전처리 및 저장을 수행합니다.
     */
    protected void transformAndSave(MetaData template, Map<String, String> variables) {
        List<Map<String, Map<String, Object>>> newRows = template.getRows().stream()
                .map(templateRow -> {
                    Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);
                    DataVariableResolver.replace(deepRow, variables);
                    return deepRow;
                }).toList();

        MetaData processedData = new MetaData(template.getTable(), newRows);
        fileReadService.dataPreProcess(processedData);
        dbProcessService.save(processedData);
    }
}
