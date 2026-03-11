package com.example.playground.cis.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class CISMockController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/transfer/transfer")
    public ResponseEntity<?> transferSend(@RequestBody String body) throws JsonProcessingException {

        log.info("request >> {}", body);

        List<Map<String, Object>> requestDatas = (List<Map<String,Object>>) objectMapper.readValue(body, List.class);

        List<TransferSendResDto.Data> datas =
                requestDatas.stream()
                        .map(requestData -> TransferSendResDto.Data.builder()
                                .resultCd("29")
                                .resultMsg("success")
                                .id(String.valueOf(requestData.get("id")))
                                .ordNo(String.valueOf(requestData.get("ordNo")))
                                .trdNo(UUID.randomUUID().toString().replace("-", "").substring(0,20))
                                .trDt(String.valueOf(requestData.get("trDt")))
                                .trTm(String.valueOf(requestData.get("trTm")))
                                .bankCd(String.valueOf(requestData.get("bankCd")))
                                .accntNo(String.valueOf(requestData.get("accntNo")))
                                .amt(String.valueOf(requestData.get("amt")))
                                .balance("0")
                                .build())
                        .toList();

        log.info("response >> {} ", datas);

        return ResponseEntity.ok(datas);
    }


    @PostMapping("/transfer/recv")
    public ResponseEntity<?> transferRecv(@RequestBody String body) throws JsonProcessingException {

        log.info("request >> {}", body);

        List<Map<String, Object>> requestDatas = (List<Map<String,Object>>) objectMapper.readValue(body, List.class);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyMMdd");
        DateTimeFormatter hms = DateTimeFormatter.ofPattern("HHmmss");

        String trDt = now.format(ymd);
        String trTm = now.format(hms);

        List<TransferRecvResDto> receives = new ArrayList<>();
        for(Map<String, Object> requestData : requestDatas){
            TransferRecvResDto data = TransferRecvResDto.builder()
                    .resultCd("21")
                    .resultMsg("success")
                    .id((String) requestData.get("id"))
                    .ordNo((String) requestData.get("ordNo"))
                    .trdNo(UUID.randomUUID().toString().substring(0,20))
                    .trDt(trDt)
                    .trTm(trTm)
                    .status("21")
                    .build();
            receives.add(data);
        }

        log.info("response >> {} ", receives);

        return ResponseEntity.ok(receives);
    }

}
