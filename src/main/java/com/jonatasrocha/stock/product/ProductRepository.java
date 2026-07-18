package com.jonatasrocha.stock.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonatasrocha.stock.product.ProductEntity.Sku;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsBySku(Sku sku);

    boolean existsBySkuAndIdNot(Sku sku, Long id);

}
