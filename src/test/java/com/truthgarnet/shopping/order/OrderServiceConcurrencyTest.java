package com.truthgarnet.shopping.order;

import java.util.List;
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

        int poolSize = 100;
        int taskCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        CountDownLatch latch = new CountDownLatch(taskCount);

        // 성공 횟수를 셀 수 있는 변수
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger outOfStockCount = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);

        long start = System.nanoTime();

        for(int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {

                try {
                    orderService.insertOrders(orderRequest);

                    success.incrementAndGet();
                } catch (IllegalArgumentException e) {
                    outOfStockCount.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                }
                finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        long end = System.nanoTime();

        System.out.println("속도 조회: " + (end - start) / 1_000_000.0 + "ms");
        System.out.println("그 외 예외" + fail);
        Assertions.assertEquals(1, success.get());
        
        Assertions.assertEquals(999, outOfStockCount.get());
    }
    
}
