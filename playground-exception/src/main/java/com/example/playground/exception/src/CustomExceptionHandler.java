package com.example.playground.exception.src;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.NoSuchElementException;


@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    //공통 처리 가능

    @ExceptionHandler(IOException.class)
    public String responseIoException(IOException e){
        log.error("IOException : {}", e.getMessage());
        return "[FAIL] IOException" + e.getMessage();
    }

    @ExceptionHandler({NullPointerException.class, NoSuchElementException.class})
    public String responseResourceException(RuntimeException e){
        log.warn("Resource Exception : {}", e.getMessage());
        return "[FAIL] NullPoiter or NoSuchElement Exception" + e.getMessage();
    }

    @ExceptionHandler(CustomValidException.class)
    public String customValidationException(CustomValidException ex){
        log.warn("Validation ex : {}", ex.getMessage());
        return ex.getErrorMap().toString();
    }

    @ExceptionHandler(CustomException.class)
    public String customException(CustomException e){
        log.warn("Custom Ex : {}", e.getMessage());
        return "[FAIL] Custom Exception" +e.getMessage();
    }
}
