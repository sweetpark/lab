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
 * 계약(Contract) 관련 데이터를 처리하는 전략 클래스입니다.
 * <p>
 * {@link AbstractMetaDataProcessStrategy}를 상속받아 공통 로직을 재사용합니다.
 */
@Order(2)
@Component
public class ContractProcessStrategy extends AbstractMetaDataProcessStrategy {

    private final TableClassifier classifier;

    public ContractProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        super(fileReadService, dbProcessService);
        this.classifier = classifier;
    }

    /**
     * 메타데이터가 계약 관련 테이블인지 확인합니다.
     */
    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isContractRelated(metaData);
    }

    /**
     * 계약 데이터를 처리합니다. 첫 번째 MID 설정을 기준으로 변수 컨텍스트를 생성하여 치환합니다.
     */
    @Override
    public void process(MetaData metaData, InitData propertiesData) {
        transformAndSave(metaData, VariableContext.getContextMap(propertiesData.getMidList().getFirst()));
    }
}
