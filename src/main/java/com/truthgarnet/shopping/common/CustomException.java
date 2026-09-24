package com.truthgarnet.shopping.common;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter 
public class CustomException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;
    private final List<FieldErrorResponse> errors;
    
    public CustomException(ErrorCode errorCode, List<FieldErrorResponse> errors) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.errors = errors;
    }
}
