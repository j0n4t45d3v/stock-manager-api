package com.jonatasrocha.stock.product;

import java.math.BigDecimal;

import com.jonatasrocha.stock.category.CategoryEntity;
import com.jonatasrocha.stock.common.BaseEntity;
import com.jonatasrocha.stock.supplier.SupplierEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_products")
public class ProductEntity extends BaseEntity {

    private String name;

    private String description;

    private Sku sku;

    private UnitPrice unitPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplier;

    protected ProductEntity() {
        super(null, null, null);
    }

    private ProductEntity(Builder builder) {
        super(builder.id, null, null);
        this.name = builder.name;
        this.description = builder.description;
        this.sku = builder.sku;
        this.unitPrice = builder.unitPrice;
        this.category = builder.category;
        this.supplier = builder.supplier;
    }

    public static Builder builder(Long id){
        return new Builder(id);
    }
    public static Builder builder(){
        return new Builder(null);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Sku getSku() {
        return sku;
    }
    
    public String getSkuValue() {
        return sku.value();
    }

    public UnitPrice getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getUnitPriceValue() {
        return unitPrice.value();
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public Long getCategoryId() {
        return category.getId();
    }

    public SupplierEntity getSupplier() {
        return supplier;
    }
    public Long getSupplierId() {
        return supplier.getId();
    }

    public static final class Builder
        implements NameStepBuilder,
                   DescriptionStepBuilder,
                   SkuStepBuilder,
                   UnitPriceStepBuilder,
                   CategoryStepBuilder,
                   SupplierStepBuilder,
                   BuildStepBuilder 
    {
        private Long id;
        private String name;
        private String description;
        private Sku sku;
        private UnitPrice unitPrice;
        private CategoryEntity category;
        private SupplierEntity supplier;

        private Builder(Long id) {
            this.id = id;
            this.name = null;
            this.description = null;
            this.sku = null;
            this.unitPrice = null;
            this.category = null;
            this.supplier = null;
        }

        @Override
        public DescriptionStepBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        @Override
        public SkuStepBuilder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public UnitPriceStepBuilder sku(String sku) {
            this.sku = new Sku(sku);
            return this;
        }

        @Override
        public SupplierStepBuilder unitPrice(BigDecimal value) {
            this.unitPrice = new UnitPrice(value);
            return this;
        }

        @Override
        public CategoryStepBuilder supplier(SupplierEntity supplier) {
            this.supplier = supplier;
            return this;
        }
        
        @Override
        public BuildStepBuilder category(CategoryEntity category) {
            this.category = category;
            return this;
        }

        @Override
        public ProductEntity build() {
            return new ProductEntity(this);
        }
    } 

    public sealed interface NameStepBuilder permits Builder {
        DescriptionStepBuilder name(String name);
    }
    public sealed interface DescriptionStepBuilder permits Builder {
        SkuStepBuilder description(String name);
    }
    public sealed interface SkuStepBuilder permits Builder{
        UnitPriceStepBuilder sku(String sku);
    }
    public sealed interface UnitPriceStepBuilder permits Builder {
        SupplierStepBuilder unitPrice(BigDecimal value);
    }
    public sealed interface SupplierStepBuilder permits Builder {
        CategoryStepBuilder supplier(SupplierEntity supplier);
    }
    public sealed interface CategoryStepBuilder permits Builder {
        BuildStepBuilder category(CategoryEntity category);
    }

    public sealed interface BuildStepBuilder permits Builder {
        ProductEntity build();
    }

    @Embeddable
    public record Sku(@Column(name="sku") String value) {}

    @Embeddable
    public record UnitPrice(@Column(name="unit_price") BigDecimal value) {}

}