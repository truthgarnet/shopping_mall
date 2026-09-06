package com.truthgarnet.shopping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.truthgarnet.shopping.product.ProductEntity;
import com.truthgarnet.shopping.product.ProductRepository;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private List<Long> productSeqs;

    @BeforeEach
    void init() {
        List<ProductEntity> products = new ArrayList<>();

        for (long i = 1; i <= 1000; i++) {
            ProductEntity product = new ProductEntity();

            product.setProductName("상품" + i);

            products.add(product);
        }

        List<ProductEntity> saved = productRepository.saveAll(products);
        productSeqs = saved.stream()
            .map(ProductEntity::getProductSeq)
            .collect(Collectors.toList());

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByProductSeq() {

        long start = System.nanoTime();

        for (Long seq : productSeqs) {
            productRepository.findByProductSeq(seq);
        }

        long end = System.nanoTime();

        System.out.println("개별 조회: " + (end - start) / 1_000_000.0 + "ms");
    }

    @Test
    void findByProductSeqIn() {
        long start = System.nanoTime();

        productRepository.findByProductSeqIn(productSeqs);


        long end = System.nanoTime();

        System.out.println("IN 조회: " + (end - start) / 1_000_000.0 + "ms");
    }
}
