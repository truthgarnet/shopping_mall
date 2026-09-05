package com.truthgarnet.shopping.orderItem;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    @NonNull
    private Long productSeq;

    @NonNull
    @Positive
    private Integer quantity; 

}
