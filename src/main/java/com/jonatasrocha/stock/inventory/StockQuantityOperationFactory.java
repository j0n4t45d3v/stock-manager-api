package com.jonatasrocha.stock.inventory;

import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class StockQuantityOperationFactory {

    private final EnumMap<MovementType, StockQuantityOperation> strategies;

    public StockQuantityOperationFactory(List<StockQuantityOperation> operations) {
        this.strategies = new EnumMap<>(MovementType.class);

        for (StockQuantityOperation operation : operations) {
            this.strategies.computeIfAbsent(operation.type(), k -> operation);
        }
    }

    public StockQuantityOperation get(MovementType type) {
        if (!strategies.containsKey(type)) {
            throw new UnsupportedOperationException("Operation unimplemented for type: " + type);
        }
        return strategies.get(type);
    }

}
