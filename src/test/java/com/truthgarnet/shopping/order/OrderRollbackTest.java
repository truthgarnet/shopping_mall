package com.truthgarnet.shopping.order;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.truthgarnet.shopping.orderItem.OrderItemRepository;
import com.truthgarnet.shopping.orderItem.OrderItemRequest;
import com.truthgarnet.shopping.product.ProductEntity;
import com.truthgarnet.shopping.product.ProductRepository;

@SpringBootTest
public class OrderRollbackTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private List<Long> saveProductsSeq = new ArrayList<>();

    @BeforeEach
    void init() {
        // given A, B의 개수 각각 10개
        productRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();

        ProductEntity productA = new ProductEntity();
        productA.setProductName("상품A");
        productA.setPrice(1000);
        productA.setStock(10);

        ProductEntity productB = new ProductEntity();
        productB.setProductName("상품B");
        productB.setPrice(2000);
        productB.setStock(0);

        List<ProductEntity> products = List.of(productA, productB);
        List<ProductEntity> saveProducts = productRepository.saveAll(products);
        saveProductsSeq = saveProducts.stream().map(entity -> entity.getProductSeq()).toList();
    }

    @Test
    public void insertOrders() {
        // when 주문 요청을 만든다.
        OrderItemRequest itemA = new OrderItemRequest(saveProductsSeq.get(0), 5);
        OrderItemRequest itemB = new OrderItemRequest(saveProductsSeq.get(1), 10);

        List<OrderItemRequest> oRequests = List.of(itemA, itemB);
        OrderRequest orderRequest = new OrderRequest(oRequests);

        assertThatThrownBy(() -> orderService.insertOrders(orderRequest)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("는 재고가 부족한 상품입니다.");

        // 롤백이 잘되어 주문이 저장되지 않았는 가, 재고가 차감되지 않았는 가
        assertThat(orderRepository.count()).isEqualTo(0);
        Optional<ProductEntity> productA = productRepository.findById(itemA.getProductSeq());
        assertThat(productA.get().getStock()).isEqualTo(10);
    }
}
