package com.truthgarnet.shopping.order;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.truthgarnet.shopping.orderItem.OrderItemRequest;
import com.truthgarnet.shopping.product.ProductEntity;
import com.truthgarnet.shopping.product.ProductRepository;

@SpringBootTest 
public class OrderServiceConcurrencyTest {

    @Autowired 
    private OrderService orderService;

    @Autowired 
    private ProductRepository productRepository;

    @Test 
    void concurrentOrders_shouldDeductStockCorrectly() throws InterruptedException {
        // 상품 저장
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName("A");
        productEntity.setPrice(1000);
        productEntity.setStock(1);

        ProductEntity saveProduct = productRepository.save(productEntity);

        OrderItemRequest orderItemRequest = new OrderItemRequest(saveProduct.getProductSeq(), 1);
        List<OrderItemRequest> list = List.of(orderItemRequest);
        OrderRequest orderRequest = new OrderRequest(list);

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 성공 횟수를 셀 수 있는 변수
        AtomicInteger success = new AtomicInteger(0);

        for(int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {

                try {
                    orderService.insertOrders(orderRequest);

                    success.incrementAndGet();
                } catch (IllegalArgumentException e) {

                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Assertions.assertEquals(1, success.get());
    }
    
}
