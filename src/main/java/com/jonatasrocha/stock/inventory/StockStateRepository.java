package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StockStateRepository extends JpaRepository<StockStateEntity, Long>{

    @Modifying
    @Query(value = """
    UPDATE StockStateEntity
    SET balance = balance - ?1
    WHERE productId = ?2
      AND balance >= ?1
    """) 
    int decreaseBalance(BigDecimal quantity, Long productId);

    @Modifying
    @Query(value = """
    UPDATE StockStateEntity SET balance = balance + ?1 WHERE productId = ?2
    """) 
    int incrementBalance(BigDecimal quantity, Long productId);

    boolean existsByProductId(Long productId);
}
