package com.truthgarnet.shopping.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionalHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<FieldErrorResponse> error = e.getFieldErrors().stream()
                .map(e1 -> new FieldErrorResponse(e1.getField(), e1.getDefaultMessage())).toList();

        ErrorResponse fieldErrorResponses = new ErrorResponse(error, "CM001", "입력값이 올바르지 않습니다.");

        return new ResponseEntity<>(fieldErrorResponses, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlerCustomException(CustomException e) {

        ErrorResponse fieldErrorResponses = new ErrorResponse(List.of(), e.getCode(), e.getMessage());

        return new ResponseEntity<>(fieldErrorResponses, e.getHttpStatus());
    }
}
