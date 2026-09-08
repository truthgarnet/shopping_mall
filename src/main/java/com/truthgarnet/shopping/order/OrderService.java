package com.truthgarnet.shopping.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.truthgarnet.shopping.order.OrderEntity.OrderStatus;
import com.truthgarnet.shopping.orderItem.OrderItemEntity;
import com.truthgarnet.shopping.orderItem.OrderItemRepository;
import com.truthgarnet.shopping.orderItem.OrderItemRequest;
import com.truthgarnet.shopping.orderItem.OrderItemResponse;
import com.truthgarnet.shopping.product.ProductEntity;
import com.truthgarnet.shopping.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional 
    public OrderResponse insertOrders(OrderRequest orderRequest) {
        // 1. order 생성
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setStatus(OrderStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();
        orderEntity.setCreatedAt(now);

        OrderEntity saveOrder = orderRepository.save(orderEntity);

        // 2. product 조회
        List<OrderItemRequest> items = orderRequest.getItems();
        List<Long> productSeqs = items.stream().map(item -> (item.getProductSeq())).collect(Collectors.toList());

        List<ProductEntity> products = productRepository.findByProductSeqInOrderByProductSeqAsc(productSeqs);

        Map<Long, ProductEntity> productMap = products.stream()
            .collect(Collectors.toMap(
                ProductEntity::getProductSeq, product -> product
            ));
        
        // 3. product 재고 빼기
        for (OrderItemRequest orderItem : orderRequest.getItems()) {
            ProductEntity product = productMap.get(orderItem.getProductSeq());
            int stock = product.getStock();

            if (stock - orderItem.getQuantity() >= 0) {
                stock -= orderItem.getQuantity();
                product.setStock(stock);
            } else {
                throw new IllegalArgumentException(product.getProductName() + " 는 재고가 부족한 상품입니다.");
            }
            productRepository.save(product);
        }

        // 4. orderItem 생성
        List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();
        for (OrderItemRequest item : orderRequest.getItems()) {
            ProductEntity product = productMap.get(item.getProductSeq());
            OrderItemEntity orderItem = new OrderItemEntity();

            orderItem.setProductSeq(item.getProductSeq());
            orderItem.setProductName(product.getProductName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setOrderSeq(saveOrder.getOrderSeq());
            orderItems.add(orderItem);
        }
        List<OrderItemEntity> orderItemEntity = orderItemRepository.saveAll(orderItems);
        
        List<OrderItemResponse> orderItemResponses = orderItemEntity.stream().map(entity -> new OrderItemResponse(entity.getOrderItemSeq(), entity.getProductSeq(), entity.getQuantity(), entity.getUnitPrice())).collect(Collectors.toList());

        return new OrderResponse(saveOrder.getOrderSeq(), orderItemResponses, saveOrder.getStatus());
    }
    
}
