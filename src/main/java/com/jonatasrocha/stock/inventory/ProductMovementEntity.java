package com.jonatasrocha.stock.inventory;

import java.math.BigDecimal;

import com.jonatasrocha.stock.common.BaseEntity;
import com.jonatasrocha.stock.product.ProductEntity.UnitPrice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_products_movements")
public class ProductMovementEntity extends BaseEntity {

    private Quantity quantity;

    private UnitPrice unitPrice;

    private MovementType type;

    private Note note;

    @Column(name = "product_id")
    private Long productId;

    protected ProductMovementEntity() {
        super(null, null, null);
    }

    private ProductMovementEntity(
        MovementType type,
        Quantity quantity,
        Note note,
        Long productId,
        UnitPrice unitPrice
    ) {
        super(null, null, null);
        this.type = type;
        this.quantity = quantity;
        this.note = note;
        this.productId = productId;
        this.unitPrice = unitPrice;
    }

    public static ProductMovementEntity ofType(
        MovementType type,
        Quantity quantity,
        Note note,
        Long productId,
        UnitPrice unitPrice
    ) {
        return new ProductMovementEntity(
            type,
            quantity,
            note,
            productId,
            unitPrice
        );
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public BigDecimal getQuantityValue() {
        return quantity.value();
    }

    public UnitPrice getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getUnitPriceValue() {
        return this.unitPrice.value();
    }

    public String getTypeName() {
        return this.type.name();
    }

    public MovementType getType() {
        return this.type;
    }

    public boolean isExit() {
        return this.type == MovementType.EXIT;
    }

    public boolean isEntry() {
        return this.type == MovementType.ENTRY;
    }

    public boolean isReserve() {
        return this.type == MovementType.RESERVE;
    }

    public Note getNote() {
        return note;
    }

    public String getNoteValue() {
        return note.value;
    }

    public Long getProductId() {
        return this.productId;
    }

    @Embeddable
    public record Quantity(@Column(name = "quantity", nullable = false) BigDecimal value) {
    }

    @Embeddable
    public record Note(@Column(name = "note", nullable = false) String value) {
    }

}
