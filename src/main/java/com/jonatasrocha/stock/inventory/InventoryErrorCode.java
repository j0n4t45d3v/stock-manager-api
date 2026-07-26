package com.jonatasrocha.stock.inventory;

import com.jonatasrocha.stock.common.ErrorCode;

enum InventoryErrorCode implements ErrorCode {

    INVENTORY_ALREADY_EXISTS("Already exists inventory for this product"),
    INSUFICCIENT_STOCK("Product not has this quantity available in stock");

    private final String message;

    private InventoryErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public String message() {
        return this.message;
    }

}
