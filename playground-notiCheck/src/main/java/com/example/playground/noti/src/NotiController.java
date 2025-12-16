package com.example.playground.noti.src;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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
}
