package com.example.playground.exception.source;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;

@Controller
public class ExceptionController {

    @GetMapping("/api/valid/exception")
    public String validException(){
        throw new CustomValidException("validException", new HashMap<>());
    }

    @GetMapping("/api/custom/exception")
    public String customException(){
        throw new CustomException("customException");
    }

    @GetMapping("/api/null/exception")
    public String nullException(){
        throw new NullPointerException("NullException");
    }
}
