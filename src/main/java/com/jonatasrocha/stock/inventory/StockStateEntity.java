package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import com.jonatasrocha.stock.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_stock_state")
public class StockStateEntity extends BaseEntity{

    private BigDecimal balance;

    @Column(name="reserved_balance")
    private BigDecimal reservedBalance;

    @Column(name="available_balance", insertable = false, updatable = false)
    private BigDecimal availableBalance;

    @Column(name = "product_id")
    private Long productId;

    protected StockStateEntity() {
        super(null, null, null);
    }

    private StockStateEntity(BigDecimal balance, Long productId) {
        super(null, null, null);
        this.balance = balance;
        this.reservedBalance = BigDecimal.ZERO;
        this.productId = productId;
    }

    public static StockStateEntity of(Long productId) {
        return new StockStateEntity(BigDecimal.ZERO, productId);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public Long getProductId() {
        return this.productId;
    } 

}
