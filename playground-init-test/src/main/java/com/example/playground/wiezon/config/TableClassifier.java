package com.example.playground.wiezon.config;

import com.example.playground.wiezon.dto.MetaData;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TableClassifier {
    private static final Set<String> AFFILIATE_TABLES = Set.of(
            "TBSI_PTN_CPID",
            "TBSI_PTN_FEE"
    );

    private static final Set<String> MID_TABLES = Set.of(
            "TBSI_MBS",
            "TBSI_MBS_PTN_LNK",
            "TBSI_MBS_KEY"
    );

    private static final Set<String> CONTRACT_TABLES = Set.of(
            "TBSI_CO",
            "TBSI_CO_PAY",
            "TBSI_STMT_FEE"
    );

    public boolean isAffiliate(MetaData metaData) {
        return AFFILIATE_TABLES.contains(metaData.getTable());
    }

    public boolean isMidRelated(MetaData metaData) {
        return MID_TABLES.contains(metaData.getTable());
    }

    public boolean isContractRelated(MetaData metaData) {
        return CONTRACT_TABLES.contains(metaData.getTable());
    }
}
