package com.example.playground.exception.src;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
