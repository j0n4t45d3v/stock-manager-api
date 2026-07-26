package com.jonatasrocha.stock.inventory;


import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovementTypeConverter implements AttributeConverter<MovementType, Short> {

    @Override
    public Short convertToDatabaseColumn(@Nonnull MovementType attribute) {
        return attribute.getValue();
    }

    @Override
    public MovementType convertToEntityAttribute(Short dbData) {
        return MovementType.fromValue(dbData);
    }

}
