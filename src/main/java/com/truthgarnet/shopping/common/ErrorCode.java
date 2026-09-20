package com.truthgarnet.shopping.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // common
    INVALID_INPUT("CM001", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    PRODUCT_NOT_FOUND("PD001", "존재하지 않는 상품입니다.",HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK("OD001", "재고가 부족한 상품이 있습니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
