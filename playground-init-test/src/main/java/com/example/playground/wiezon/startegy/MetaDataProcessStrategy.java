package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;

import java.util.Map;

/**
 * 메타데이터 처리를 위한 전략 인터페이스입니다.
 * <p>
 * Strategy Pattern을 사용하여 테이블(데이터) 종류별로 다른 처리 로직을 캡슐화합니다.
 */
public interface MetaDataProcessStrategy {

    /**
     * 해당 전략이 주어진 메타데이터를 처리할 수 있는지 여부를 반환합니다.
     *
     * @param metaData 처리할 메타데이터
     * @return 처리 가능 여부
     */
    boolean supports(MetaData metaData);

    /**
     * 메타데이터와 초기화 데이터를 사용하여 비즈니스 로직을 수행합니다.
     *
     * @param template       JSON 템플릿 데이터
     * @param propertiesData 초기화에 필요한 설정 데이터 (MID, CPID 등)
     */
    void process(MetaData template, InitData propertiesData);

    /**
     * 템플릿의 특정 컬럼 값이 플레이스홀더(예: "${MID}")와 일치하는지 확인합니다.
     *
     * @param row         데이터 행
     * @param colKey      컬럼 키
     * @param templateStr 비교할 템플릿 문자열
     * @return 일치 여부
     */
    default boolean isTemplateMatched(Map<String, Map<String, Object>> row, String colKey, String templateStr) {
        if (!row.containsKey(colKey) || row.get(colKey) == null) return false;

        Object valueObj = row.get(colKey).get("value");
        return valueObj != null && valueObj.equals(templateStr);
    }
}