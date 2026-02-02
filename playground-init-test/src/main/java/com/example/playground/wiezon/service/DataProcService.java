package com.example.playground.wiezon.service;

import com.example.playground.wiezon.dto.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataProcService {

    @Autowired
    private Environment environment;
    private final static Set<String> tableChk = Set.of("TBSI_PTN_CPID", "TBSI_PTN_FEE");


    // 제휴사 관련 전처리
    public List<MetaData> affiliateProcess(MetaData template){

        List<MetaData> resultList = new ArrayList<>();

        if(tableChk.contains(template.getTable())){

                int index = 0;

                while(true){
                    String ptnCd = environment.getProperty(String.format("cpids[%d].ptnCd", index));
                    if(ptnCd == null){
                        break;
                    }
                    String keyType = environment.getProperty(String.format("cpids[%d].keyType", index));
                    String key = environment.getProperty(String.format("cpids[%d].key", index));


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
                    index++;
                }
        }else{
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
