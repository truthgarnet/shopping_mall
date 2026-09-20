package com.truthgarnet.shopping.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter 
public class CustomException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;
    
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }
}
