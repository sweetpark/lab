package com.example.playground.wiezon.startegy;

import com.example.playground.wiezon.config.TableClassifier;
import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import com.example.playground.wiezon.service.DBProcessService;
import com.example.playground.wiezon.service.FileReadService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


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

            //CPID properties 값 load
            String ptnCd = cpidMap.getPtnCd();
            String keyType = cpidMap.getKeyType();
            String key = cpidMap.getKey();

            // template 에서 해당 값 바꿔치기
            List<Map<String, Map<String, Object>>> newRows = template.getRows().stream()
                    .map(templateRow -> {

                        Map<String, Map<String, Object>> deepRow = new HashMap<>();

                        // 깊은 복사
                        for (String _key : templateRow.keySet()) {
                            Map<String, Object> innerMap = templateRow.get(_key);
                            deepRow.put(_key, new HashMap<>(innerMap));
                        }

                        if (isTemplateMatched(deepRow, "PTN_CD", "${PTN_CD}")) {
                            deepRow.put("PTN_CD", Map.of("value", ptnCd));
                        }

                        if(isTemplateMatched(deepRow, "KEY_TYPE", "${KEY_TYPE}")
                                && isTemplateMatched(deepRow, "KEY", "${KEY}"))
                        {
                            deepRow.put("KEY_TYPE", Map.of("value", Objects.requireNonNull(keyType)));
                            deepRow.put("KEY", Map.of("value", Objects.requireNonNull(key)));
                        }


                        return deepRow;
                    })
                    .collect(Collectors.toList());


            resultList.add(new MetaData(template.getTable(), newRows));
        }


        return resultList;
    }
}
