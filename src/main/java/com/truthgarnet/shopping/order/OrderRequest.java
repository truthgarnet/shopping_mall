package com.truthgarnet.shopping.order;

import java.util.List;

import com.truthgarnet.shopping.orderItem.OrderItemRequest;

import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class OrderRequest {
    List<OrderItemRequest> items;

    
    @Valid
    void checkDupliSeq() {
        
    }
}
