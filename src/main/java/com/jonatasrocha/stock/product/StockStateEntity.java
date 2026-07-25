package com.jonatasrocha.stock.product;

import java.math.BigDecimal;

import com.jonatasrocha.stock.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_stock_state")
public class StockStateEntity extends BaseEntity{

    private BigDecimal balance;

    @OneToOne
    private ProductEntity product;

    protected StockStateEntity() {
        super(null, null, null);
    }

    private StockStateEntity(BigDecimal balance, ProductEntity product) {
        super(null, null, null);
        this.balance = balance;
        this.product = product;
    }

    public static StockStateEntity of(ProductEntity productEntity) {
        return new StockStateEntity(BigDecimal.ZERO, productEntity);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public ProductEntity getProduct() {
        return product;
    } 

}
