package com.truthgarnet.shopping.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
public class FieldErrorResponse {

    private String field;
    private String message;
    
}
