package com.truthgarnet.shopping.orderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByOrderSeq(Long orderSeq);
    
}
