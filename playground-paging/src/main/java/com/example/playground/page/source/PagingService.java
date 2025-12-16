package com.example.playground.page.source;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PagingService {

    private final TemperatureRepository temperatureRepository;
    private static int TOTCNT = 0;

    public void save(){
        for (int i = 0; i < 10000; i++) {
            Temperature temperature = new Temperature("testTemp" + i, "name" + i);
            temperatureRepository.save(temperature);
        }
    }


    public Map<String, Object> findBasicPage(int currentPage, int limit){
        Map<String ,Object> map = new HashMap<>();
        int offset = (currentPage - 1) * limit;

        map.put("content", temperatureRepository.findByPage(limit, offset));
        map.put("totCnt", temperatureRepository.getTotCnt());
        return map;
    }

    //범위기반 페이징 조회
    public Map<String, Object> findPageToBatch(int currentPage, int limit, int batch){
        Map<String, Object> map = new HashMap();
        int offset = (currentPage - 1) * limit;
//        if(currentPage*limit > batch){
//            batch += limit * 10;
//            map.put("RangePageCnt", temperatureRepository.rangePageCnt(batch, offset));
//        }

//        if ((currentPage * limit) > batch) {
//            // 단순 증가 말고 고정 구간 계산
//            int newBatch = ((currentPage * limit) / (limit * 10) + 1) * (limit * 10);
//            batch = newBatch;
//
//            // 일정 간격에서만 count 호출
//            if (currentPage % 10 == 0) {
////                map.put("RangePageCnt", temperatureRepository.rangePageCnt(currentPage, batch));
//                map.put("RangePageCnt", temperatureRepository.getTotCnt());
//            }
//        }
        if ((currentPage * limit) > batch) {
            batch += limit * 10;
            map.put("RangePageCnt", temperatureRepository.getTotCnt());
        }

        map.put("batch", batch);
        map.put("content", temperatureRepository.findByPage(limit, offset));
        return map;
    }



    //메모리에 cnt를 갱신해서 사용
    public Map<String, Object> findPageToSchedule(int currentPage, int limit){
        Map<String, Object> map = new HashMap<>();
        int offset = (currentPage - 1) * limit;
        map.put("totCnt", TOTCNT);
        map.put("content", temperatureRepository.findByPage(limit,offset));

        return map;
    }

    //커서 기반 페이징 (OFFSET x)
    public Map<String, Object> findPageToCursor(Long lastIndex, int limit) {
        Map<String, Object> map = new HashMap<>();

        if (lastIndex == null) lastIndex = 0L;

        List<Temperature> result = temperatureRepository.cursorPage(lastIndex, limit);
        map.put("content", result);

        if (!result.isEmpty()) {
            Long nextCursor = result.get(result.size() - 1).getId();
            map.put("lastIndex", nextCursor);
        } else {
            map.put("lastIndex", null);
        }

        return map;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public synchronized void ScheduleTotCnt(){
        TOTCNT = temperatureRepository.getTotCnt();
    }



}
