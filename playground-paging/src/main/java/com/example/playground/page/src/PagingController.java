package com.example.playground.page.src;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PagingController {

    private final PagingService pagingService;

    @GetMapping("/basic/offset")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> basicOffset(@RequestParam("currentPage") Integer currentPage){
        return ResponseEntity.ok(pagingService.findBasicPage(currentPage, Constants.LIMIT));
    }

    @GetMapping("/range/offset")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rangeOffset(@RequestParam("currentPage") Integer currentPage,
                                                               @RequestParam("range") Integer range) {
        return ResponseEntity.ok(pagingService.findPageToBatch(currentPage, Constants.LIMIT, range));
    }



    @GetMapping("/schedule/offset")
    public ResponseEntity<Map<String, Object>> scheduleOffset(@RequestParam("currentPage") Integer currentPage){
        return ResponseEntity.ok(pagingService.findPageToSchedule(currentPage, Constants.LIMIT));
    }


    @GetMapping("/cursor")
    public ResponseEntity<Map<String ,Object>> cursorPage(@RequestParam("lastIndex") Long lastIndex){
        return ResponseEntity.ok(pagingService.findPageToCursor(lastIndex, Constants.LIMIT));
    }




}
