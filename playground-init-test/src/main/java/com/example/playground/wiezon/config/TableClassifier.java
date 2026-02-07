package com.example.playground.wiezon.config;

import com.example.playground.wiezon.dto.MetaData;
import org.springframework.stereotype.Component;


/**
 * 메타데이터(테이블)가 어떤 종류의 데이터인지(MID, GID, CONTRACT 등)를 분류해주는 클래스입니다.
 * <p>
 * 전략({@link com.example.playground.wiezon.startegy.MetaDataProcessStrategy}) 선택 시 사용됩니다.
 */
@Component
public class TableClassifier {

    public boolean isAffiliate(MetaData metaData) {
        return metaData.getDivision().equalsIgnoreCase("AFFILIATE");
    }

    public boolean isMidRelated(MetaData metaData) {
        return metaData.getDivision().equalsIgnoreCase("MID");
    }

    public boolean isGidRelated(MetaData metaData){
        return metaData.getDivision().equalsIgnoreCase("GID");
    }

    public boolean isVidRelated(MetaData metaData){
        return metaData.getDivision().equalsIgnoreCase("VID");
    }

    public boolean isContractRelated(MetaData metaData) {
        return metaData.getDivision().equalsIgnoreCase("CONTRACT");
    }
}