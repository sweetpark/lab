package com.example.playground.noti.source;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@Slf4j
public class NotiController {


    @PostMapping("/testNoti.do")
    public void notification(
            HttpServletRequest request,
            @RequestHeader Map<String, String> headers,
            @RequestBody(required = false) String body)  {


        log.info("========== [HEADERS] ==========");
        headers.forEach((key, value) -> log.info("{} : {}", key, value));

        log.info("========== [BODY] ==========");
        log.info("body :{}", body);

        log.info("Method: {}", request.getMethod());

        // 4. 요청 URL
        log.info("Request URL: {}", request.getRequestURL());

        // 5. 요청 URI
        log.info("Request URI: {}", request.getRequestURI());

        // 6. QueryString
        log.info("QueryString: {}", request.getQueryString());

        // 7. Content-Type
        log.info("Content-Type: {}", request.getContentType());

        // 8. 클라이언트 IP
        log.info("Client IP: {}", request.getRemoteAddr());


        log.info("===== END =====");

    }


    @PostMapping("/testNotiNo.do")
    public ResponseEntity<?> no_notification(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid Request");
    }

    @PostMapping("/loki/api/v1/push")
    public ResponseEntity<?> loki_push(HttpServletRequest request){

        try {
            ServletInputStream inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while((line = reader.readLine()) != null){
                log.info("[BODY] : {}", line);
            }

        } catch (IOException e) {
            log.error("body parsing error");
        }

        log.info("ParamterMap : {}", request.getParameterMap());

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }
}
