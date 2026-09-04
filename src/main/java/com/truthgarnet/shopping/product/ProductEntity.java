package com.truthgarnet.shopping.product;

import java.time.LocalDateTime;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;

@Entity
@Setter
@Getter
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productSeq;

    private String productName;
    private Integer price;
    private Integer stock;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
