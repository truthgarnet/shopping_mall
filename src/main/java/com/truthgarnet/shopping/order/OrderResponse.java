package com.truthgarnet.shopping.order;

import java.util.List;

import com.truthgarnet.shopping.order.OrderEntity.OrderStatus;
import com.truthgarnet.shopping.orderItem.OrderItemResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
    
    private Long orderSeq;
    private List<OrderItemResponse> orderItemResponse;
    private OrderStatus orderStatus;

}
