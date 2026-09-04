package com.truthgarnet.shopping.orderItem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private Long orderItemSeq;
    private Long productSeq;
    private Integer quantity;
    private Integer unitPrice;

    public Integer getTotalPrice() {
        return unitPrice * quantity;
    }
}
