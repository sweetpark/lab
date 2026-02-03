package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.CpidMap;
import com.example.playground.wiezon.dto.InitData;
import com.example.playground.wiezon.dto.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataProcService {

    private final static Set<String> affiliateChk = Set.of("TBSI_PTN_CPID", "TBSI_PTN_FEE");


    // 제휴사 관련 전처리
    public List<MetaData> affiliateProcess(MetaData template, InitData propertiesData){

        List<MetaData> resultList = new ArrayList<>();

        if(affiliateChk.contains(template.getTable())){
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

                                if (isTemplateMatched(deepRow, "PTN_CD", "${PTN_CD}")
                                        || isTemplateMatched(deepRow, "KEY_TYPE", "${KEY_TYPE}")
                                        || isTemplateMatched(deepRow, "KEY", "${KEY}"))
                                {
                                    deepRow.put("PTN_CD", Map.of("value", ptnCd));
                                    deepRow.put("KEY_TYPE", Map.of("value", Objects.requireNonNull(keyType)));
                                    deepRow.put("KEY", Map.of("value", Objects.requireNonNull(key)));
                                }

                                return deepRow;
                            })
                            .collect(Collectors.toList());


                    resultList.add(new MetaData(template.getTable(), newRows));
                }

        }else{
            // 제휴사 테이블이 아닐경우, 그대로 넘김
            resultList.add(template);
        }


        return resultList;
    }

    private boolean isTemplateMatched(Map<String, Map<String, Object>> row, String colKey, String templateStr) {
        if (!row.containsKey(colKey) || row.get(colKey) == null) return false;

        Object valueObj = row.get(colKey).get("value");
        return valueObj != null && valueObj.equals(templateStr);
    }
}
