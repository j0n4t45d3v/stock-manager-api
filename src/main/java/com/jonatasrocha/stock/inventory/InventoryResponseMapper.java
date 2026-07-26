package com.jonatasrocha.stock.inventory;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.infra.http.Mapper;
import com.jonatasrocha.stock.inventory.InventoryController.InventoryResponse;

@Component
public class InventoryResponseMapper implements Mapper<ProductMovementEntity, InventoryResponse> {

    @Override
    public InventoryResponse map(ProductMovementEntity input) {
        return new InventoryResponse(
            input.getTypeName(),
            input.getQuantityValue(),
            input.getNoteValue(),
            input.getProductId(),
            input.getUnitPriceValue()
        );
    }

}
