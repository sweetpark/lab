package com.example.playground.wiezon.exception;

public class FileParseException extends RuntimeException {

    public FileParseException(String message) {
        super(message);
    }

    public FileParseException(String message, Exception e) {
        super(message, e);
    }
}
