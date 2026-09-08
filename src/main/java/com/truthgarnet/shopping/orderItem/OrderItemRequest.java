package com.truthgarnet.shopping.orderItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    @NotNull
    @Positive 
    private Long productSeq;

    @NotNull
    @Positive
    private Integer quantity; 

}
