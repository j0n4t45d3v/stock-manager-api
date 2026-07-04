package com.jonatasrocha.stock.category;

import java.time.Instant;

import com.jonatasrocha.stock.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_categories")
public class CategoryEntity extends BaseEntity {

    private String name;

    protected CategoryEntity() {
        this(null, null,null, null);
    }

    private CategoryEntity(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
    ) {
        super(id, createdAt, updatedAt);
        this.name = name;
    }

    public static CategoryEntity of(String name){
        return CategoryEntity.of(null, name);
    }

    public static CategoryEntity of(Long id, String name){
        return new CategoryEntity(
            id, 
            name,
            null,
            null
        );
    }

    public String getName() {
        return this.name;
    }

}
