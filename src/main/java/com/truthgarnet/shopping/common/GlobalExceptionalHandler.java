package com.truthgarnet.shopping.common;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestControllerAdvice
public class GlobalExceptionalHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<FieldErrorResponse> error = e.getFieldErrors().stream()
                .map(e1 -> new FieldErrorResponse(e1.getField(), e1.getDefaultMessage())).toList();

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        ErrorResponse fieldErrorResponses = new ErrorResponse(error, errorCode.getCode(), errorCode.getMessage());

        return new ResponseEntity<>(fieldErrorResponses, errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerServerException(Exception e) {

        List<FieldErrorResponse> error = List.of();

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse fieldErrorResponses = new ErrorResponse(error, errorCode.getCode(), errorCode.getMessage());

        log.error("서버 에러: ", e);

        return new ResponseEntity<>(fieldErrorResponses, errorCode.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlerCustomException(CustomException e) {
        ErrorResponse fieldErrorResponses = new ErrorResponse(e.getErrors(), e.getCode(), e.getMessage());

        return new ResponseEntity<>(fieldErrorResponses, e.getHttpStatus());
    }
}
