package com.example.playground.wiezon.config;

import com.example.playground.wiezon.dto.MetaData;
import org.springframework.stereotype.Component;

import java.util.Set;

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
