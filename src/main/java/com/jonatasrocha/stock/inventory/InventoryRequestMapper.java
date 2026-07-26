package com.jonatasrocha.stock.inventory;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.infra.http.Mapper;
import com.jonatasrocha.stock.inventory.InventoryController.InventoryRequest;
import com.jonatasrocha.stock.inventory.ProductMovementEntity.Note;
import com.jonatasrocha.stock.inventory.ProductMovementEntity.Quantity;
import com.jonatasrocha.stock.product.ProductEntity.UnitPrice;

@Component
public class InventoryRequestMapper implements Mapper<InventoryRequest, ProductMovementEntity> {

    @Override
    public ProductMovementEntity map(InventoryRequest input) {
        return ProductMovementEntity.ofType(
            input.type(),
            new Quantity(input.quantity()),
            new Note(input.note()),
            input.productId(),
            new UnitPrice(input.unitPrice())
        );
    }

}
