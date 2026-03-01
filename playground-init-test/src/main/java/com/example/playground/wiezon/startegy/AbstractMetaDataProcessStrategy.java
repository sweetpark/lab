package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.context.TemplateContext;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import com.example.playground.wiezon.util.DataVariableResolver;

import java.util.List;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;

/**
 * 메타데이터 처리 전략의 공통 기능을 제공하는 추상 클래스입니다.
 * <p>
 * Template Method Pattern과 유사하게 공통된 데이터 변환 및 저장 흐름({@link #transformAndSave})을 정의하고,
 * 하위 클래스에서 구체적인 변수 매핑(Context)을 제공하도록 유도합니다.
 */
public abstract class AbstractMetaDataProcessStrategy implements MetaDataProcessStrategy {

    protected final FileReadService fileReadService;
    protected final DBProcessService dbProcessService;

    protected AbstractMetaDataProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService) {
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }

    /**
     * 템플릿 데이터의 변수(${...})를 치환하고, 전처리 후 DB에 저장합니다.
     * <p>
     * 1. 템플릿의 각 행을 순회하며 깊은 복사(Deep Copy)를 수행합니다.
     * 2. {@link DataVariableResolver}를 사용하여 변수를 실제 값으로 치환합니다.
     * 3. {@link FileReadService#dataPreProcess}를 통해 날짜/암호화 등의 전처리를 수행합니다.
     * 4. {@link DBProcessService#save}를 통해 DB에 저장합니다.
     *
     * @param template  처리할 템플릿 메타데이터
     * @param variables 치환에 사용할 변수 맵 (Key: "${VAR}", Value: "ActualValue")
     */
    protected void transformAndSave(TemplateContext template, Map<String, Object> variables) {
        List<Map<String, Map<String, Object>>> newRows = template.getRows().stream()
                .map(templateRow -> {
                    Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);
                    DataVariableResolver.replace(deepRow, variables);
                    return deepRow;
                }).toList();

        TemplateContext processedData = new TemplateContext(template.getTable(), newRows);
        fileReadService.dataPreProcess(processedData);
        dbProcessService.save(processedData);
    }
}