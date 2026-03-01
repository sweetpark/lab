package com.example.playground.wiezon.config;

import com.example.playground.wiezon.Enum.Division;
import com.example.playground.wiezon.context.TemplateContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


/**
 * 메타데이터(테이블)가 어떤 종류의 데이터인지(MID, GID, CONTRACT 등)를 분류해주는 클래스입니다.
 * <p>
 * 전략({@link com.example.playground.wiezon.startegy.MetaDataProcessStrategy}) 선택 시 사용됩니다.
 */
@Component
public class TableClassifier {

    private static final Set<String> BASIC_DIVISIONS = Set.of(
            Division.MID.name(),
            Division.GID.name(),
            Division.VID.name(),
            Division.AFFILIATE.name(),
            Division.CONTRACT.name(),
            Division.PAY.name()
    );
    public boolean isBasicRelated(TemplateContext templateContext) {
        String division = templateContext.getDivision();
        return division != null && BASIC_DIVISIONS.contains(division.toUpperCase());
    }
    public boolean isSupportRelated(TemplateContext templateContext){ return templateContext.getDivision().equalsIgnoreCase("SUPPORT"); }
}