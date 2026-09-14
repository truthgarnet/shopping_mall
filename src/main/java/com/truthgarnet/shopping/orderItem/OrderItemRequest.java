package com.truthgarnet.shopping.orderItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    
    @NotNull(message = "상품 번호를 입력해주세요.")
    @Positive(message = "상품 번호는 1부터 입니다.") 
    private Long productSeq;

    @NotNull(message = "수량을 입력해야 합니다.")
    @Positive(message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity; 

}
