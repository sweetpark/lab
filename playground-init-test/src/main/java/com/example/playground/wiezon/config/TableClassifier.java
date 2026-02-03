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
        return metaData.getDivision().equalsIgnoreCase("USER");
    }

    public boolean isContractRelated(MetaData metaData) {
        return metaData.getDivision().equalsIgnoreCase("CONTRACT");
    }
}
