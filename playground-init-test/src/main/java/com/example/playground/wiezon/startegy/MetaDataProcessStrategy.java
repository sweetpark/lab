package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.context.TemplateContext;

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
     * @param templateContext 처리할 메타데이터
     * @return 처리 가능 여부
     */
    boolean supports(TemplateContext templateContext);

    /**
     * 메타데이터와 초기화 데이터를 사용하여 비즈니스 로직을 수행합니다.
     *
     * @param template       JSON 템플릿 데이터
     * @param context 초기화에 필요한 설정 데이터 (MID, CPID 등)
     */
    void process(TemplateContext template, Map<String, Object> context);
}