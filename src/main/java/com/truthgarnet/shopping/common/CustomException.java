package com.truthgarnet.shopping.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter 
public class CustomException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;
    
    public CustomException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
