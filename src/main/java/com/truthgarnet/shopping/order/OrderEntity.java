package com.truthgarnet.shopping.order;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderSeq;
    private OrderStatus status;

    private String productName;
    private Integer unitPrice;
    private Integer quantity;

    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt;

    public enum OrderStatus{
        PENDING,  // 주문 대기 
        COMPLETED,  // 주문 완료
        FAIL, // 주문 실패
        CANCEL // 주문 취소
    }
}
