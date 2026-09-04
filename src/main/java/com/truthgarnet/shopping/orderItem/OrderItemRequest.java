package com.truthgarnet.shopping.orderItem;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderItemRequest {
    @NonNull
    private Long productSeq;

    @NonNull
    @Positive
    private Integer quantity; 

}
