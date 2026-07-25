package com.jonatasrocha.stock.product;

import com.jonatasrocha.stock.product.ProductMovementEntity.Type;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovementTypeConverter implements AttributeConverter<ProductMovementEntity.Type, Short> {

    @Override
    public Short convertToDatabaseColumn(@Nonnull Type attribute) {
        return attribute.getValue();
    }

    @Override
    public Type convertToEntityAttribute(Short dbData) {
        return dbData == null
            ? null
            : ProductMovementEntity.Type.fromValue(dbData);
    }

}
