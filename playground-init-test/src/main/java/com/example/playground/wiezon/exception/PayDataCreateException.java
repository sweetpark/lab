package com.example.playground.wiezon.exception;

public class PayDataCreateException extends RuntimeException {
    public PayDataCreateException(String message) {
        super(message);
    }

    public PayDataCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
