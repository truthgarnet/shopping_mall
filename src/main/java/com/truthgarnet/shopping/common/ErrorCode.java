package com.truthgarnet.shopping.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter 
public enum ErrorCode {
    PRODUCT_NOT_FOUND("PD001", HttpStatus.BAD_REQUEST), 
    OUT_OF_STOCK("OD001", HttpStatus.CONFLICT);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
} 
