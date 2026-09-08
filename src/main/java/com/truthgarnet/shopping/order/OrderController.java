package com.truthgarnet.shopping.order;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping("/orders")
    public OrderResponse insertOrders(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.insertOrders(orderRequest);
        return orderResponse;
    }
    
}
