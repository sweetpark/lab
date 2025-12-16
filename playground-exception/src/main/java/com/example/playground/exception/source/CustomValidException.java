package com.example.playground.exception.source;

import lombok.Getter;
import java.util.Map;

@Getter
public class CustomValidException extends RuntimeException{
    private Map<String, String> errorMap;

    public CustomValidException(String message, Map<String, String> errorMap){
        super(message);
        this.errorMap = errorMap;
    }

}
