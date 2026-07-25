package com.jonatasrocha.stock.product;

import java.math.BigDecimal;

import com.jonatasrocha.stock.common.BaseEntity;
import com.jonatasrocha.stock.product.ProductEntity.UnitPrice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_products_movements")
public class ProductMovementEntity extends BaseEntity {

    private Quantity quantity;

    private UnitPrice unitPrice;

    private Type type;

    private Note note;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    protected ProductMovementEntity() {
        super(null, null, null);
    }

    private ProductMovementEntity(
        Type type,
        Quantity quantity,
        Note note,
        ProductEntity productEntity,
        UnitPrice unitPrice
    ) {
        super(null, null, null);
        this.type = type;
        this.quantity = quantity;
        this.note = note;
        this.product = productEntity;
        this.unitPrice = unitPrice == null ?  productEntity.getUnitPrice() : unitPrice;
    }

    public static ProductMovementEntity ofType(Type type,Quantity quantity, Note note, ProductEntity productEntity, UnitPrice unitPrice) {
        return new ProductMovementEntity(
            type,
            quantity,
            note,
            productEntity,
            unitPrice
        );
    }

    public static ProductMovementEntity ofExit(Quantity quantity, Note note, ProductEntity productEntity) {
        return new ProductMovementEntity(Type.EXIT, quantity, note, productEntity, null);
    }

    public static ProductMovementEntity ofEntry(Quantity quantity, Note note, ProductEntity productEntity) {
        return new ProductMovementEntity(Type.ENTRY, quantity, note, productEntity, null);
    }

    public static ProductMovementEntity ofReserve(Quantity quantity, Note note, ProductEntity productEntity) {
        return new ProductMovementEntity(Type.RESERVE, quantity, note, productEntity, null);
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

    public boolean isExit() {
        return this.type == Type.EXIT;
    }

    public boolean isEntry() {
        return this.type == Type.ENTRY;
    }

    public boolean isReserve() {
        return this.type == Type.RESERVE;
    }

    public Note getNote() {
        return note;
    }

    public String getNoteValue() {
        return note.value;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public Long getProductId() {
        return product.getId();
    }

    @Embeddable
    public record Quantity(@Column(name = "quantity", nullable = false) BigDecimal value) {
    }

    @Embeddable
    public record Note(@Column(name = "note", nullable = false) String value) {
    }

    enum Type {
        ENTRY((short)0, false),
        EXIT((short)1, true),
        RESERVE((short)2, true);

        private final short value;
        private final boolean decreaseValue;

        private Type(short value, boolean decreaseValue) {
            this.value = value;
            this.decreaseValue = decreaseValue;
        } 

        public short getValue() {
            return this.value;
        }

        public boolean isDecrease() {
            return this.decreaseValue;
        }

		public static Type fromValue(Short value) {
            switch (value) {
                case 0:
                    return Type.ENTRY;
                case 1:
                    return  Type.EXIT;
                case 2:
                    return Type.RESERVE;
                default:
                    return null;
            }
		}
    }

}
