package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.dto.MidInitData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.playground.wiezon.util.CommonUtil.createNewRow;

@Component
public class MerchantProcessStrategy implements MetaDataProcessStrategy{

    private final TableClassifier classifier;
    private final FileReadService fileReadService;
    private final DBProcessService dbProcessService;

    public MerchantProcessStrategy(TableClassifier classifier, FileReadService fileReadService, DBProcessService dbProcessService) {
        this.classifier = classifier;
        this.fileReadService = fileReadService;
        this.dbProcessService = dbProcessService;
    }


    @Override
    public boolean supports(MetaData metaData) {
        return classifier.isMidRelated(metaData);
    }

    @Override
    public void process(MetaData template, InitData propertiesData) {
            merchantProcess(template,propertiesData).forEach(
                    processed -> {
                        fileReadService.dataPreProcess(processed);
                        dbProcessService.save(processed);
                    }
            );
    }


    private List<MetaData> merchantProcess(MetaData template, InitData propertiesData){
        List<MetaData> resultList = new ArrayList<>();

        for(MidInitData midInitData :propertiesData.getMidList()){

            List<Map<String, Map<String,Object>>> newRows = template.getRows().stream()
                    .map( templateRow -> {
                        Map<String, Map<String, Object>> deepRow = createNewRow(templateRow);

                        //FIXME) cpid가 없는경우 처리 로직 추가 필요

                        replace(deepRow, "UID", "${MID}", midInitData.getMid());
                        replace(deepRow, "MID", "${MID}", midInitData.getMid());
                        replace(deepRow, "CO_NO", "${CO_NO}", midInitData.getCono());

                        // Cert
                        replace(deepRow, "PTN_CD", "${CERT_PTN_CD}", midInitData.getCpidMap().getCertPtnCd());
                        replace(deepRow, "PTN_CPID", "${CERTIFICATION_CPID}", midInitData.getCpidMap().getCertCpid());

                        // Old Cert
                        replace(deepRow, "PTN_CD", "${OLD_PTN_CD}", midInitData.getCpidMap().getOldCertPtnCd());
                        replace(deepRow, "PTN_CPID", "${OLD_CERT_CPID}", midInitData.getCpidMap().getOldCertCpid());

                        // No Cert
                        replace(deepRow, "PTN_CD", "${NO_CERT_PTN_CD}", midInitData.getCpidMap().getNoCertPtnCd());
                        replace(deepRow, "PTN_CPID", "${NO_CERT_CPID}", midInitData.getCpidMap().getNoCertCpid());

                        // Offline
                        replace(deepRow, "PTN_CD", "${OFFLINE_PTN_CD}", midInitData.getCpidMap().getOfflinePtnCd());
                        replace(deepRow, "PTN_CPID", "${OFFLINE_CPID}", midInitData.getCpidMap().getOfflineCpid());

                        return deepRow;
                    }).toList();

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
