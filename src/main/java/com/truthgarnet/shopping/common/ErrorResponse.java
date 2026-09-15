package com.truthgarnet.shopping.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
public class ErrorResponse {

    private List<FieldErrorResponse> errors;
    private String code;
    private String message;
    
}
