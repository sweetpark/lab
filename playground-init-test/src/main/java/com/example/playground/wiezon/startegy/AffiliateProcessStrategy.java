package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;

@Order(1)
@Component
public class AffiliateProcessStrategy implements MetaDataProcessStrategy{

    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;
    private final TableClassifier classifier;

    public AffiliateProcessStrategy(FileReadService fileReadService, DBProcessService dbProcessService, TableClassifier classifier) {
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
        this.classifier = classifier;
    }

    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isAffiliate(metaData);
    }

    // 제휴사 Insert 로직
    @Override
    public void process(MetaData template, InitData propertiesData) {
        affiliateProcess(template, propertiesData)
                .forEach(processed -> {
                    fileReadService.dataPreProcess(processed);
                    dbProcessService.save(processed);
                });
    }


    private List<MetaData> affiliateProcess(MetaData template, InitData propertiesData){

        List<MetaData> resultList = new ArrayList<>();

        for(CpidMap cpidMap : propertiesData.getCpidList() ){

            // template 에서 해당 값 바꿔치기
            List<Map<String, Map<String, Object>>> newRows = template.getRows().stream()
                    .map(templateRow -> {

                        Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);

                        //FIXME) cpid가 없는경우 처리 로직 추가 필요

                        // Cert
                        replace(deepRow, "PTN_CD", "${CERT_PTN_CD}", cpidMap.getCertPtnCd());
                        replace(deepRow, "PTN_CPID", "${CERTIFICATION_CPID}", cpidMap.getCertCpid());
                        replace(deepRow, "KEY_TYPE", "${CERT_KEY_TYPE}", cpidMap.getCertKeyType());
                        replace(deepRow, "DATA", "${CERT_KEY}", cpidMap.getCertKey());

                        // Old Cert
                        replace(deepRow, "PTN_CD", "${OLD_PTN_CD}", cpidMap.getOldCertPtnCd());
                        replace(deepRow, "PTN_CPID", "${OLD_CERT_CPID}", cpidMap.getOldCertCpid());
                        replace(deepRow, "KEY_TYPE", "${OLD_KEY_TYPE}", cpidMap.getOldCertKeyType());
                        replace(deepRow, "DATA", "${OLD_KEY}", cpidMap.getOldCertKey());

                        // No Cert
                        replace(deepRow, "PTN_CD", "${NO_CERT_PTN_CD}", cpidMap.getNoCertPtnCd());
                        replace(deepRow, "PTN_CPID", "${NO_CERT_CPID}", cpidMap.getNoCertCpid());
                        replace(deepRow, "KEY_TYPE", "${NO_CERT_KEY_TYPE}", cpidMap.getNoCertKeyType());
                        replace(deepRow, "DATA", "${NO_CERT_KEY}", cpidMap.getNoCertKey());

                        // Offline
                        replace(deepRow, "PTN_CD", "${OFFLINE_PTN_CD}", cpidMap.getOfflinePtnCd());
                        replace(deepRow, "PTN_CPID", "${OFFLINE_CPID}", cpidMap.getOfflineCpid());
                        replace(deepRow, "KEY_TYPE", "${OFFLINE_KEY_TYPE}", cpidMap.getOfflineKeyType());
                        replace(deepRow, "DATA", "${OFFLINE_KEY}", cpidMap.getOfflineKey());

                        return deepRow;
                    })
                    .collect(Collectors.toList());


            resultList.add(new MetaData(template.getTable(), newRows));
        }


        return resultList;
    }

    private void replace(Map<String, Map<String, Object>> row, String col, String template, String value) {
        if (isTemplateMatched(row, col, template)) {
            row.put(col, Map.of("value", value == null ? "" : value));
        }
    }
}
