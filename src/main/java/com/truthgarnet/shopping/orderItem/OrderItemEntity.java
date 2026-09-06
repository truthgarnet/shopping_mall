package com.truthgarnet.shopping.orderItem;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemSeq;
    private Long productSeq;
    private Long orderSeq;
    private String productName;
    private Integer unitPrice;
    private Integer quantity;

}
