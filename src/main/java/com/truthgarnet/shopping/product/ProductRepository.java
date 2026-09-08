package com.truthgarnet.shopping.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    Optional<ProductEntity> findByProductSeq(Long seq);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ProductEntity> findByProductSeqInOrderByProductSeqAsc(List<Long> seqs);

}
