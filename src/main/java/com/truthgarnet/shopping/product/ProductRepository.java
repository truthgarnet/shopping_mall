package com.truthgarnet.shopping.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    Optional<ProductEntity> findByProductSeq(Long seq);

    List<ProductEntity> findByProductSeqIn(List<Long> seqs);

}
