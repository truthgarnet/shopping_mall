package com.truthgarnet.shopping.order;

import java.util.List;

import com.truthgarnet.shopping.orderItem.OrderItemRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    List<OrderItemRequest> items;

    
    @Valid
    void checkDupliSeq() {
        
    }
}
