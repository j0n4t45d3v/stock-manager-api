package com.jonatasrocha.stock.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProductMovementRepository extends JpaRepository<ProductMovementEntity, Long> {

    @Query("""
            SELECT pm
            FROM
                ProductMovementEntity pm
            WHERE pm.productId = ?1
              AND (?2 is null OR pm.id < ?2)
            ORDER BY 
                pm.id DESC
            """)
    Page<ProductMovementEntity> findAllMovementByProduct(Long productId, Long offset, Pageable pageable);
}
